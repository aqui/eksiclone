package in.batur.eksiclone.statisticsservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import in.batur.eksiclone.statisticsservice.dto.PopularItemDTO;
import in.batur.eksiclone.statisticsservice.dto.StatisticsDTO;
import in.batur.eksiclone.statisticsservice.dto.TopicStatisticsDTO;
import in.batur.eksiclone.statisticsservice.dto.UserStatisticsDTO;

public interface StatisticsService {
    StatisticsDTO getOverallStatistics();
    
    StatisticsDTO getDailyStatistics(LocalDate date);
    
    Map<LocalDate, StatisticsDTO> getStatisticsOverTime(LocalDate fromDate, LocalDate toDate);
    
    UserStatisticsDTO getUserStatistics(Long userId);
    
    TopicStatisticsDTO getTopicStatistics(Long topicId);
    
    List<PopularItemDTO> getPopularTopics(int limit);
    
    List<PopularItemDTO> getPopularEntries(int limit);
    
    List<PopularItemDTO> getActiveUsers(int limit);
    
    List<PopularItemDTO> getPopularTags(int limit);
}
