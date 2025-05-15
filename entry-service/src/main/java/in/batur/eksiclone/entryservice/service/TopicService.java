package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entryservice.dto.CreateTopicRequest;
import in.batur.eksiclone.entryservice.dto.TopicDTO;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicService {
    TopicDTO createTopic(CreateTopicRequest request);
    
    TopicDTO getTopic(Long id);
    
    TopicDTO getTopicByTitle(String title);
    
    TopicDTO updateTopic(Long id, String description, Set<String> tags);
    
    void deleteTopic(Long id);
    
    Page<TopicDTO> getLatestTopics(Pageable pageable);
    
    Page<TopicDTO> getPopularTopics(Pageable pageable);
    
    Page<TopicDTO> getMostViewedTopics(Pageable pageable);
    
    Page<TopicDTO> getTopicsByTag(String tagName, Pageable pageable);
    
    Page<TopicDTO> searchTopics(String query, Pageable pageable);
    
    void incrementViewCount(Long topicId);
}