package in.batur.eksiclone.moderationservice.service;

import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.moderationservice.dto.ModerationStatsDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModerationStatsService {
    
    private final ReportService reportService;
    private final UserModerationService userModerationService;
    private final ModeratorActionService moderatorActionService;
    
    public ModerationStatsService(
            ReportService reportService,
            UserModerationService userModerationService,
            ModeratorActionService moderatorActionService) {
        this.reportService = reportService;
        this.userModerationService = userModerationService;
        this.moderatorActionService = moderatorActionService;
    }
    
    @Transactional(readOnly = true)
    public ModerationStatsDTO getStats() {
        // Count pending reports
        long pendingReports = reportService.countReportsByStatus(ReportStatus.PENDING);
        
        // Count active moderations
        long activeModerationsCount = userModerationService.countActiveModerations();
        
        // Reports by status
        Map<String, Long> reportsByStatus = Arrays.stream(ReportStatus.values())
                .collect(Collectors.toMap(
                        ReportStatus::name,
                        status -> reportService.countReportsByStatus(status)
                ));
        
        // Reports by content type
        Map<String, Long> reportsByContentType = new HashMap<>(); // Placeholder - in real app would use repository
        
        // Moderations by type
        Map<String, Long> moderationsByType = userModerationService.countActiveByType();
        
        // Actions by type
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Map<String, Long> actionsByType = moderatorActionService.countActionsByTypeInDateRange(oneMonthAgo, LocalDateTime.now());
        
        // Actions by moderator
        Map<String, Long> actionsByModerator = moderatorActionService.countActionsByModerator();
        
        return ModerationStatsDTO.builder()
                .pendingReports(pendingReports)
                .activeModerationsCount(activeModerationsCount)
                .reportsByStatus(reportsByStatus)
                .reportsByContentType(reportsByContentType)
                .moderationsByType(moderationsByType)
                .actionsByType(actionsByType)
                .actionsByModerator(actionsByModerator)
                .build();
    }
}
