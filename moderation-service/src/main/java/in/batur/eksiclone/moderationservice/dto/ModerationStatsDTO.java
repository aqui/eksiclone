package in.batur.eksiclone.moderationservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationStatsDTO {
    private long pendingReports;
    private long activeModerationsCount;
    private Map<String, Long> reportsByStatus;
    private Map<String, Long> reportsByContentType;
    private Map<String, Long> moderationsByType;
    private Map<String, Long> actionsByType;
    private Map<String, Long> actionsByModerator;
}
