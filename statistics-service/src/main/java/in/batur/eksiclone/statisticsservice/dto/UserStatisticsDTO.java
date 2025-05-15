package in.batur.eksiclone.statisticsservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private Long userId;
    private String username;
    private Long totalEntries;
    private Long totalFavorites;
    private Long receivedFavorites;
    private Double averageFavoritesPerEntry;
    private LocalDateTime registrationDate;
    private LocalDateTime lastActiveDate;
    private Map<String, Long> entryCountByTag;
    private Map<LocalDateTime, Long> activityOverTime;
}
