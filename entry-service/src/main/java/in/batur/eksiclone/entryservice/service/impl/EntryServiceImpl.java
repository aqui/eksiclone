package in.batur.eksiclone.entryservice.service.impl;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entity.entry.Topic;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.entryservice.dto.CreateEntryRequest;
import in.batur.eksiclone.entryservice.dto.EntryDTO;
import in.batur.eksiclone.entryservice.mapper.EntryMapper;
import in.batur.eksiclone.entryservice.service.EntryService;
import in.batur.eksiclone.entryservice.util.TagNormalizer;
import in.batur.eksiclone.repository.entry.EntryRepository;
import in.batur.eksiclone.repository.entry.TagRepository;
import in.batur.eksiclone.repository.entry.TopicRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final EntryMapper entryMapper;
    private final TagNormalizer tagNormalizer;

    public EntryServiceImpl(
            EntryRepository entryRepository, 
            TopicRepository topicRepository, 
            UserRepository userRepository, 
            TagRepository tagRepository,
            EntryMapper entryMapper,
            TagNormalizer tagNormalizer) {
        this.entryRepository = entryRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.entryMapper = entryMapper;
        this.tagNormalizer = tagNormalizer;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"entries", "topics"}, allEntries = true)
    public EntryDTO createEntry(CreateEntryRequest request) {
        validateCreateEntryRequest(request);
        
        // Find topic
        Topic topic = findTopicById(request.getTopicId());
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create entry for a deleted topic");
        }
        
        // Find author
        User author = findUserById(request.getAuthorId());
        
        // Create and setup entry
        Entry entry = new Entry();
        entry.setContent(request.getContent());
        entry.setTopic(topic);
        entry.setAuthor(author);
        
        // Handle tags if present
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<String> normalizedTags = normalizeTagSet(request.getTags());
            addTagsToEntry(entry, normalizedTags);
        }
        
        // Save entry
        entry = entryRepository.save(entry);
        
        // Update topic
        topic.addEntry(entry);
        topicRepository.save(topic);
        
        return entryMapper.toDto(entry);
    }

    private void validateCreateEntryRequest(CreateEntryRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content is required");
        }
        
        if (request.getTopicId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic ID is required");
        }
        
        if (request.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author ID is required");
        }
    }
    
    private Topic findTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
    }
    
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    private Set<String> normalizeTagSet(Set<String> tagNames) {
        return tagNames.stream()
                .map(tagNormalizer::normalize)
                .collect(Collectors.toSet());
    }

    private void addTagsToEntry(Entry entry, Set<String> tagNames) {
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            entry.addTag(tag);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'entry:' + #id")
    public EntryDTO getEntry(Long id) {
        Entry entry = findEntryById(id);
        
        if (entry.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }
        
        return entryMapper.toDto(entry);
    }
    
    private Entry findEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"entries", "topics"}, key = "'entry:' + #id")
    public EntryDTO updateEntry(Long id, String content, Set<String> tagNames) {
        Entry entry = findEntryById(id);
        
        if (entry.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }
        
        // Update content if provided
        if (content != null && !content.trim().isEmpty()) {
            entry.setContent(content);
        }
        
        // Update tags if provided
        if (tagNames != null) {
            // Clear existing tags
            entry.getTags().clear();
            Set<String> normalizedTags = normalizeTagSet(tagNames);
            addTagsToEntry(entry, normalizedTags);
        }
        
        entry = entryRepository.save(entry);
        
        return entryMapper.toDto(entry);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"entries", "topics"}, allEntries = true)
    public void deleteEntry(Long id) {
        Entry entry = findEntryById(id);
        
        if (entry.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry already deleted");
        }
        
        // Soft delete
        entry.setDeleted(true);
        entryRepository.save(entry);
        
        // Update topic's entry count
        Topic topic = entry.getTopic();
        topic.removeEntry(entry);
        topicRepository.save(topic);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'byTopic:' + #topicId + ':' + #pageable")
    public Page<EntryDTO> getEntriesByTopic(Long topicId, Pageable pageable) {
        Topic topic = findTopicById(topicId);
        
        if (topic.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        
        return entryRepository.findByTopicAndIsDeletedFalseOrderByCreatedDateDesc(topic, pageable)
                .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'byAuthor:' + #authorId + ':' + #pageable")
    public Page<EntryDTO> getEntriesByAuthor(Long authorId, Pageable pageable) {
        User author = findUserById(authorId);
        
        return entryRepository.findByAuthorAndIsDeletedFalseOrderByCreatedDateDesc(author, pageable)
                .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'byTag:' + #tagName + ':' + #pageable")
    public Page<EntryDTO> getEntriesByTag(String tagName, Pageable pageable) {
        String normalizedTagName = tagNormalizer.normalize(tagName);
        return entryRepository.findByTagNameAndNotDeleted(normalizedTagName, pageable)
                .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'latest:' + #pageable")
    public Page<EntryDTO> getLatestEntries(Pageable pageable) {
        return entryRepository.findByIsDeletedFalseOrderByCreatedDateDesc(pageable)
                .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "entries", key = "'popular:' + #pageable")
    public Page<EntryDTO> getPopularEntries(Pageable pageable) {
        return entryRepository.findByIsDeletedFalseOrderByFavoriteCountDesc(pageable)
                .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> searchEntries(String query, Pageable pageable) {
        return entryRepository.findByContentContainingIgnoreCaseAndIsDeletedFalse(query, pageable)
                .map(entryMapper::toDto);
    }
    
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"entries", "topics"}, key = "'entry:' + #id")
    public EntryDTO incrementFavoriteCount(Long id) {
        Entry entry = findEntryById(id);
        
        if (entry.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }
        
        entry.incrementFavoriteCount();
        entry = entryRepository.save(entry);
        
        return entryMapper.toDto(entry);
    }
    
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"entries", "topics"}, key = "'entry:' + #id")
    public EntryDTO decrementFavoriteCount(Long id) {
        Entry entry = findEntryById(id);
        
        if (entry.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }
        
        entry.decrementFavoriteCount();
        entry = entryRepository.save(entry);
        
        return entryMapper.toDto(entry);
    }
}