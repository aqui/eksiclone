package in.batur.eksiclone.topicservice.service;

import in.batur.eksiclone.entity.Topic;
import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.repository.TopicRepository;
import in.batur.eksiclone.repository.UserRepository;
import in.batur.eksiclone.topicservice.dto.TopicDTO;
import in.batur.eksiclone.topicservice.exception.TopicNotFoundException;
import in.batur.eksiclone.topicservice.mapper.TopicMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final TopicMapper topicMapper;
    private final TopicMessagingService messagingService;

    public TopicServiceImpl(
        TopicRepository topicRepository, 
        UserRepository userRepository, 
        TopicMapper topicMapper,
        TopicMessagingService messagingService
    ) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.topicMapper = topicMapper;
        this.messagingService = messagingService;
    }

    @Override
    public TopicDTO createTopic(TopicDTO topicDTO, Long userId) {
        if (topicRepository.existsByTitleIgnoreCase(topicDTO.getTitle())) {
            throw new IllegalArgumentException("Topic with this title already exists");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setCreatedBy(user);
        topic.setViewCount(0L);
        topic.setEntryCount(0L);
        topic.setTrending(false);
        
        Topic savedTopic = topicRepository.save(topic);
        messagingService.sendTopicCreatedEvent(savedTopic);
        
        return topicMapper.toDto(savedTopic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDTO getTopicByTitle(String title) {
        Topic topic = topicRepository.findByTitleIgnoreCase(title)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with title: " + title));
        
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopics(Pageable pageable) {
        return topicRepository.findByOrderByCreatedDateDesc(pageable)
            .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getTrendingTopics(Pageable pageable) {
        return topicRepository.findTrending(pageable)
            .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getRecentPopularTopics(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return topicRepository.findRecentPopular(startDate, pageable)
            .map(topicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> searchTopics(String keyword, Pageable pageable) {
        return topicRepository.searchByTitle(keyword, pageable)
            .map(topicMapper::toDto);
    }

    @Override
    public TopicDTO updateTopic(Long id, TopicDTO topicDTO) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        // Check if the title is being changed and if the new title is taken
        if (!topic.getTitle().equalsIgnoreCase(topicDTO.getTitle()) && 
            topicRepository.existsByTitleIgnoreCase(topicDTO.getTitle())) {
            throw new IllegalArgumentException("Topic with this title already exists");
        }
        
        topicMapper.updateEntity(topicDTO, topic);
        Topic updatedTopic = topicRepository.save(topic);
        messagingService.sendTopicUpdatedEvent(updatedTopic);
        
        return topicMapper.toDto(updatedTopic);
    }

    @Override
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        messagingService.sendTopicDeletedEvent(topic);
        topicRepository.delete(topic);
    }

    @Override
    public TopicDTO incrementViewCount(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        topic.setViewCount(topic.getViewCount() + 1);
        return topicMapper.toDto(topicRepository.save(topic));
    }

    @Override
    public TopicDTO incrementEntryCount(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        topic.setEntryCount(topic.getEntryCount() + 1);
        Topic updatedTopic = topicRepository.save(topic);
        
        // Check if this topic should now be trending
        checkAndUpdateTrendingStatus(updatedTopic);
        
        return topicMapper.toDto(updatedTopic);
    }

    @Override
    public TopicDTO decrementEntryCount(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        if (topic.getEntryCount() > 0) {
            topic.setEntryCount(topic.getEntryCount() - 1);
        }
        
        Topic updatedTopic = topicRepository.save(topic);
        
        // Check if this topic should no longer be trending
        checkAndUpdateTrendingStatus(updatedTopic);
        
        return topicMapper.toDto(updatedTopic);
    }

    private void checkAndUpdateTrendingStatus(Topic topic) {
        // Simple logic: if a topic has more than 10 entries in the last 24 hours, mark it as trending
        // This would be more sophisticated in a real application
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        if (topic.getEntryCount() > 10 && topic.getLastUpdatedDate().isAfter(oneDayAgo)) {
            if (!topic.isTrending()) {
                topic.setTrending(true);
                topicRepository.save(topic);
            }
        } else {
            if (topic.isTrending()) {
                topic.setTrending(false);
                topicRepository.save(topic);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getTopicsByTag(String tag, Pageable pageable) {
        return topicRepository.findByTag(tag, pageable)
            .map(topicMapper::toDto);
    }

    @Override
    public TopicDTO addTags(Long id, Set<String> tags) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        if (topic.getTags() == null) {
            topic.setTags(new HashSet<>());
        }
        
        topic.getTags().addAll(tags);
        Topic updatedTopic = topicRepository.save(topic);
        messagingService.sendTopicUpdatedEvent(updatedTopic);
        
        return topicMapper.toDto(updatedTopic);
    }

    @Override
    public TopicDTO removeTags(Long id, Set<String> tags) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + id));
        
        if (topic.getTags() != null) {
            topic.getTags().removeAll(tags);
            Topic updatedTopic = topicRepository.save(topic);
            messagingService.sendTopicUpdatedEvent(updatedTopic);
            
            return topicMapper.toDto(updatedTopic);
        }
        
        return topicMapper.toDto(topic);
    }
}