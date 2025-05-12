package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entity.Entry;
import in.batur.eksiclone.entity.Topic;
import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.entryservice.dto.EntryDTO;
import in.batur.eksiclone.entryservice.exception.EntryNotFoundException;
import in.batur.eksiclone.entryservice.exception.TopicNotFoundException;
import in.batur.eksiclone.entryservice.mapper.EntryMapper;
import in.batur.eksiclone.repository.EntryRepository;
import in.batur.eksiclone.repository.TopicRepository;
import in.batur.eksiclone.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final EntryMapper entryMapper;
    private final EntryMessagingService messagingService;
    private final TopicServiceClient topicServiceClient;

    public EntryServiceImpl(
        EntryRepository entryRepository,
        TopicRepository topicRepository,
        UserRepository userRepository,
        EntryMapper entryMapper,
        EntryMessagingService messagingService,
        TopicServiceClient topicServiceClient
    ) {
        this.entryRepository = entryRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.entryMapper = entryMapper;
        this.messagingService = messagingService;
        this.topicServiceClient = topicServiceClient;
    }

    @Override
    public EntryDTO createEntry(EntryDTO entryDTO) {
        Topic topic = topicRepository.findById(entryDTO.getTopicId())
            .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + entryDTO.getTopicId()));
        
        User author = userRepository.findById(entryDTO.getAuthorId())
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + entryDTO.getAuthorId()));
        
        Entry entry = entryMapper.toEntity(entryDTO);
        entry.setTopic(topic);
        entry.setAuthor(author);
        entry.setFavoriteCount(0L);
        entry.setEdited(false);
        
        Entry savedEntry = entryRepository.save(entry);
        
        // Increment entry count in the topic
        topicServiceClient.incrementEntryCount(topic.getId());
        
        // Send message about entry creation
        messagingService.sendEntryCreatedEvent(savedEntry);
        
        return entryMapper.toDto(savedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public EntryDTO getEntryById(Long id) {
        Entry entry = entryRepository.findById(id)
            .orElseThrow(() -> new EntryNotFoundException("Entry not found with id: " + id));
        
        return entryMapper.toDto(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> getEntriesByTopicId(Long topicId, Pageable pageable) {
        return entryRepository.findByTopicId(topicId, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> getEntriesByTopicIdOrderByFavorites(Long topicId, Pageable pageable) {
        return entryRepository.findByTopicIdOrderByFavoriteCount(topicId, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> getEntriesByTopicIdOrderByNewest(Long topicId, Pageable pageable) {
        return entryRepository.findByTopicIdOrderByCreatedDateDesc(topicId, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> getEntriesByAuthorId(Long authorId, Pageable pageable) {
        return entryRepository.findByAuthorId(authorId, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> searchEntries(String keyword, Pageable pageable) {
        return entryRepository.searchByContent(keyword, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntryDTO> getPopularEntries(Pageable pageable) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return entryRepository.findPopularEntries(oneWeekAgo, pageable)
            .map(entryMapper::toDto);
    }

    @Override
    public EntryDTO updateEntry(Long id, EntryDTO entryDTO) {
        Entry entry = entryRepository.findById(id)
            .orElseThrow(() -> new EntryNotFoundException("Entry not found with id: " + id));
        
        // Check if the entry is being moved to a different topic
        if (!entry.getTopic().getId().equals(entryDTO.getTopicId())) {
            Topic newTopic = topicRepository.findById(entryDTO.getTopicId())
                .orElseThrow(() -> new TopicNotFoundException("Topic not found with id: " + entryDTO.getTopicId()));
            
            // Decrement entry count in old topic
            topicServiceClient.decrementEntryCount(entry.getTopic().getId());
            
            // Change the topic
            entry.setTopic(newTopic);
            
            // Increment entry count in new topic
            topicServiceClient.incrementEntryCount(newTopic.getId());
        }
        
        // Update the content
        entry.setContent(entryDTO.getContent());
        entry.setEdited(true);
        
        Entry updatedEntry = entryRepository.save(entry);
        messagingService.sendEntryUpdatedEvent(updatedEntry);
        
        return entryMapper.toDto(updatedEntry);
    }

    @Override
    public void deleteEntry(Long id) {
        Entry entry = entryRepository.findById(id)
            .orElseThrow(() -> new EntryNotFoundException("Entry not found with id: " + id));
        
        // Decrement entry count in the topic
        topicServiceClient.decrementEntryCount(entry.getTopic().getId());
        
        // Send message before deletion
        messagingService.sendEntryDeletedEvent(entry);
        
        entryRepository.delete(entry);
    }

    @Override
    public EntryDTO incrementFavoriteCount(Long id) {
        Entry entry = entryRepository.findById(id)
            .orElseThrow(() -> new EntryNotFoundException("Entry not found with id: " + id));
        
        entry.setFavoriteCount(entry.getFavoriteCount() + 1);
        Entry updatedEntry = entryRepository.save(entry);
        
        return entryMapper.toDto(updatedEntry);
    }

    @Override
    public EntryDTO decrementFavoriteCount(Long id) {
        Entry entry = entryRepository.findById(id)
            .orElseThrow(() -> new EntryNotFoundException("Entry not found with id: " + id));
        
        if (entry.getFavoriteCount() > 0) {
            entry.setFavoriteCount(entry.getFavoriteCount() - 1);
        }
        
        Entry updatedEntry = entryRepository.save(entry);
        
        return entryMapper.toDto(updatedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countEntriesByTopicId(Long topicId) {
        return entryRepository.countByTopicId(topicId);
    }
}