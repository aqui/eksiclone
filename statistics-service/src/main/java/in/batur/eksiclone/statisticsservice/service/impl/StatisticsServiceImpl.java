package in.batur.eksiclone.statisticsservice.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import in.batur.eksiclone.statisticsservice.dto.PopularItemDTO;
import in.batur.eksiclone.statisticsservice.dto.StatisticsDTO;
import in.batur.eksiclone.statisticsservice.dto.TopicStatisticsDTO;
import in.batur.eksiclone.statisticsservice.dto.UserStatisticsDTO;
import in.batur.eksiclone.statisticsservice.service.StatisticsService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();

    @Override
    public StatisticsDTO getOverallStatistics() {
        // Mock overall statistics
        Map<String, Long> entryCountByTag = new HashMap<>();
        entryCountByTag.put("programming", 12500L);
        entryCountByTag.put("politics", 8700L);
        entryCountByTag.put("science", 6200L);
        entryCountByTag.put("technology", 10300L);
        entryCountByTag.put("philosophy", 4800L);
        
        Map<String, Long> userCountByRole = new HashMap<>();
        userCountByRole.put("USER", 95000L);
        userCountByRole.put("MODERATOR", 50L);
        userCountByRole.put("ADMIN", 5L);
        
        Map<LocalDateTime, Long> activityOverTime = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 24; i++) {
            activityOverTime.put(now.minusHours(i), 100L + random.nextInt(900));
        }
        
        return StatisticsDTO.builder()
                .totalUsers(95055L)
                .totalTopics(28450L)
                .totalEntries(842300L)
                .activeUsers(12500L)
                .newUsersToday(120L)
                .entriesCreatedToday(8500L)
                .topicsCreatedToday(250L)
                .entryCountByTag(entryCountByTag)
                .userCountByRole(userCountByRole)
                .activityOverTime(activityOverTime)
                .build();
    }

    @Override
    public StatisticsDTO getDailyStatistics(LocalDate date) {
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }
        
        // Mock daily statistics for the specified date
        Map<String, Long> entryCountByTag = new HashMap<>();
        entryCountByTag.put("programming", 500L + random.nextInt(500));
        entryCountByTag.put("politics", 300L + random.nextInt(300));
        entryCountByTag.put("science", 200L + random.nextInt(200));
        entryCountByTag.put("technology", 400L + random.nextInt(400));
        entryCountByTag.put("philosophy", 100L + random.nextInt(100));
        
        Map<String, Long> userCountByRole = new HashMap<>();
        userCountByRole.put("USER", 100L + random.nextInt(100));
        userCountByRole.put("MODERATOR", 1L + random.nextInt(3));
        userCountByRole.put("ADMIN", 1L);
        
        Map<LocalDateTime, Long> activityOverTime = new HashMap<>();
        LocalDateTime startOfDay = date.atStartOfDay();
        for (int i = 0; i < 24; i++) {
            activityOverTime.put(startOfDay.plusHours(i), 10L + random.nextInt(90));
        }
        
        return StatisticsDTO.builder()
                .totalUsers(95055L) // Cumulative
                .totalTopics(28450L) // Cumulative
                .totalEntries(842300L) // Cumulative
                .activeUsers(500L + random.nextInt(500))
                .newUsersToday(10L + random.nextInt(50))
                .entriesCreatedToday(200L + random.nextInt(300))
                .topicsCreatedToday(5L + random.nextInt(15))
                .entryCountByTag(entryCountByTag)
                .userCountByRole(userCountByRole)
                .activityOverTime(activityOverTime)
                .build();
    }

    @Override
    public Map<LocalDate, StatisticsDTO> getStatisticsOverTime(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From date and to date are required");
        }
        
        if (fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From date must be before to date");
        }
        
        // Mock statistics over time
        Map<LocalDate, StatisticsDTO> statisticsOverTime = new HashMap<>();
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            statisticsOverTime.put(currentDate, getDailyStatistics(currentDate));
            currentDate = currentDate.plusDays(1);
        }
        
        return statisticsOverTime;
    }

    @Override
    public UserStatisticsDTO getUserStatistics(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid user ID is required");
        }
        
        // Mock user statistics
        Map<String, Long> entryCountByTag = new HashMap<>();
        entryCountByTag.put("programming", 25L + random.nextInt(50));
        entryCountByTag.put("politics", 10L + random.nextInt(30));
        entryCountByTag.put("science", 15L + random.nextInt(40));
        entryCountByTag.put("technology", 30L + random.nextInt(60));
        entryCountByTag.put("philosophy", 5L + random.nextInt(20));
        
        Map<LocalDateTime, Long> activityOverTime = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 30; i++) {
            activityOverTime.put(now.minusDays(i), 1L + random.nextInt(10));
        }
        
        Long totalEntries = entryCountByTag.values().stream().mapToLong(Long::longValue).sum();
        Long receivedFavorites = totalEntries * (1 + random.nextInt(5));
        
        return UserStatisticsDTO.builder()
                .userId(userId)
                .username("user" + userId)
                .totalEntries(totalEntries)
                .totalFavorites(50L + random.nextInt(100))
                .receivedFavorites(receivedFavorites)
                .averageFavoritesPerEntry(totalEntries > 0 ? (double) receivedFavorites / totalEntries : 0.0)
                .registrationDate(LocalDateTime.now().minusMonths(3 + random.nextInt(24)))
                .lastActiveDate(LocalDateTime.now().minusDays(random.nextInt(7)))
                .entryCountByTag(entryCountByTag)
                .activityOverTime(activityOverTime)
                .build();
    }

    @Override
    public TopicStatisticsDTO getTopicStatistics(Long topicId) {
        if (topicId == null || topicId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid topic ID is required");
        }
        
        // Mock topic statistics
        Map<String, Long> entryCountByTag = new HashMap<>();
        entryCountByTag.put("programming", 10L + random.nextInt(30));
        entryCountByTag.put("politics", 5L + random.nextInt(20));
        entryCountByTag.put("science", 8L + random.nextInt(25));
        entryCountByTag.put("technology", 15L + random.nextInt(40));
        entryCountByTag.put("philosophy", 3L + random.nextInt(15));
        
        Map<LocalDateTime, Long> activityOverTime = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 30; i++) {
            activityOverTime.put(now.minusDays(i), 10L + random.nextInt(90));
        }
        
        Long entryCount = 50L + random.nextInt(200);
        Long uniqueContributors = 10L + random.nextInt(30);
        
        return TopicStatisticsDTO.builder()
                .topicId(topicId)
                .title("Topic " + topicId)
                .entryCount(entryCount)
                .viewCount(entryCount * (5 + random.nextInt(20)))
                .uniqueContributors(uniqueContributors)
                .creationDate(LocalDateTime.now().minusMonths(1 + random.nextInt(12)))
                .lastActivityDate(LocalDateTime.now().minusDays(random.nextInt(7)))
                .entryCountByTag(entryCountByTag)
                .activityOverTime(activityOverTime)
                .build();
    }

    @Override
    public List<PopularItemDTO> getPopularTopics(int limit) {
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock popular topics
        List<PopularItemDTO> popularTopics = new ArrayList<>();
        for (int i = 1; i <= limit; i++) {
            popularTopics.add(PopularItemDTO.builder()
                    .type("TOPIC")
                    .id((long) i)
                    .name("Popular Topic " + i)
                    .score(1000L + random.nextInt(9000))
                    .build());
        }
        
        // Sort by score in descending order
        return popularTopics.stream()
                .sorted((item1, item2) -> Long.compare(item2.getScore(), item1.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PopularItemDTO> getPopularEntries(int limit) {
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock popular entries
        List<PopularItemDTO> popularEntries = new ArrayList<>();
        for (int i = 1; i <= limit; i++) {
            popularEntries.add(PopularItemDTO.builder()
                    .type("ENTRY")
                    .id((long) i)
                    .name("This is a popular entry preview text... " + i)
                    .score(100L + random.nextInt(900))
                    .build());
        }
        
        // Sort by score in descending order
        return popularEntries.stream()
                .sorted((item1, item2) -> Long.compare(item2.getScore(), item1.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PopularItemDTO> getActiveUsers(int limit) {
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock active users
        List<PopularItemDTO> activeUsers = new ArrayList<>();
        for (int i = 1; i <= limit; i++) {
            activeUsers.add(PopularItemDTO.builder()
                    .type("USER")
                    .id((long) (100 + i))
                    .name("user" + (100 + i))
                    .score(50L + random.nextInt(150))
                    .build());
        }
        
        // Sort by score in descending order
        return activeUsers.stream()
                .sorted((item1, item2) -> Long.compare(item2.getScore(), item1.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PopularItemDTO> getPopularTags(int limit) {
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock popular tags
        List<String> tags = List.of("programming", "politics", "science", "technology", "philosophy", 
                "psychology", "religion", "history", "art", "music", 
                "literature", "sports", "food", "travel", "health");
        
        List<PopularItemDTO> popularTags = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, tags.size()); i++) {
            popularTags.add(PopularItemDTO.builder()
                    .type("TAG")
                    .id((long) (i + 1))
                    .name(tags.get(i))
                    .score(500L + random.nextInt(2000))
                    .build());
        }
        
        // Sort by score in descending order
        return popularTags.stream()
                .sorted((item1, item2) -> Long.compare(item2.getScore(), item1.getScore()))
                .collect(Collectors.toList());
    }
}
