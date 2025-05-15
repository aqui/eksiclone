package in.batur.eksiclone.entryservice.service.impl;

import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entity.entry.Topic;
import in.batur.eksiclone.entryservice.dto.CreateTopicRequest;
import in.batur.eksiclone.entryservice.dto.TopicDTO;
import in.batur.eksiclone.entryservice.mapper.TopicMapper;
import in.batur.eksiclone.entryservice.service.TopicService;
import in.batur.eksiclone.repository.entry.TagRepository;
import in.batur.eksiclone.repository.entry.TopicRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TopicMapper topicMapper;

    public TopicServiceImpl(
            TopicRepository topicRepository, 
            TagRepository tagRepository, 
            UserRepository userRepository,
            TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.topicMapper = topicMapper;
    }

    @Override
    @Transactional
    public TopicDTO createTopic(CreateTopicRequest request) {
        validateCreateTopicRequest(request);
        
        // Check if topic already exists
        if (topicRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic with this title already exists");
        }
        
        // Validate author exists - but we don't need to store the reference here since
        // the first entry with this author will be created elsewhere
        userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // Create and setup topic
        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        
        // Handle tags if present
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            addTagsToTopic(topic, request.getTags());
        }
        
        // Save topic
        topic = topicRepository.save(topic);
        
        return topicMapper.toDto(topic);
    }

    private void validateCreateTopicRequest(CreateTopicRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        
        if (request.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author ID is required");
        }
    }

    private void addTagsToTopic(Topic topic, Set<String> tagNames) {
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            topic.addTag(tag);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDTO getTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDTO getTopicByTitle(String title) {
        Topic topic = topicRepository.findByTitleIgnoreCase(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public TopicDTO updateTopic(Long id, String description, Set<String> tagNames) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        
        // Update description if provided
        if (description != null) {
            topic.setDescription(description);
        }
        
        // Update tags if provided
        if (tagNames != null) {
            // Clear existing tags
            topic.getTags().clear();
            addTagsToTopic(topic, tagNames);
        }
        
        topic = topicRepository.save(topic);
        
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic already deleted");
        }
        
        // Soft delete the topic
        topic.setDeleted(true);
        topicRepository.save(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getLatestTopics(Pageable pageable) {
        return topicRepository.findByIsDeletedFalseOrderByCreatedDateDesc(pageable)
                .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getPopularTopics(Pageable pageable) {
        return topicRepository.findByIsDeletedFalseOrderByEntryCountDesc(pageable)
                .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getMostViewedTopics(Pageable pageable) {
        return topicRepository.findByIsDeletedFalseOrderByViewCountDesc(pageable)
                .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getTopicsByTag(String tagName, Pageable pageable) {
        return topicRepository.findByTagNameAndNotDeleted(tagName, pageable)
                .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> searchTopics(String query, Pageable pageable) {
        return topicRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(query, pageable)
                .map(topicMapper::toDto);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        
        if (!topic.isDeleted()) {
            topic.incrementViewCount();
            topicRepository.save(topic);
        }
    }
}