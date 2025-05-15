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
public class StatisticsDTO {
    private Long totalUsers;
    private Long totalTopics;
    private Long totalEntries;
    private Long activeUsers;
    private Long newUsersToday;
    private Long entriesCreatedToday;
    private Long topicsCreatedToday;
    private Map<String, Long> entryCountByTag;
    private Map<String, Long> userCountByRole;
    private Map<LocalDateTime, Long> activityOverTime;
}
