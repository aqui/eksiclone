package in.batur.eksiclone.topicservice.service;

import in.batur.eksiclone.topicservice.dto.TopicDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface TopicService {
    TopicDTO createTopic(TopicDTO topicDTO, Long userId);
    
    TopicDTO getTopicById(Long id);
    
    TopicDTO getTopicByTitle(String title);
    
    Page<TopicDTO> getAllTopics(Pageable pageable);
    
    Page<TopicDTO> getTrendingTopics(Pageable pageable);
    
    Page<TopicDTO> getRecentPopularTopics(Pageable pageable);
    
    Page<TopicDTO> searchTopics(String keyword, Pageable pageable);
    
    TopicDTO updateTopic(Long id, TopicDTO topicDTO);
    
    void deleteTopic(Long id);
    
    TopicDTO incrementViewCount(Long id);
    
    TopicDTO incrementEntryCount(Long id);
    
    TopicDTO decrementEntryCount(Long id);
    
    Page<TopicDTO> getTopicsByTag(String tag, Pageable pageable);
    
    TopicDTO addTags(Long id, Set<String> tags);
    
    TopicDTO removeTags(Long id, Set<String> tags);
}