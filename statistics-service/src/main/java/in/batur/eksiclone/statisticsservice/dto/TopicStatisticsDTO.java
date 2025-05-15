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
public class TopicStatisticsDTO {
    private Long topicId;
    private String title;
    private Long entryCount;
    private Long viewCount;
    private Long uniqueContributors;
    private LocalDateTime creationDate;
    private LocalDateTime lastActivityDate;
    private Map<String, Long> entryCountByTag;
    private Map<LocalDateTime, Long> activityOverTime;
}
