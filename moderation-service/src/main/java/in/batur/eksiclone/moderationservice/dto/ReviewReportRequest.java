package in.batur.eksiclone.moderationservice.dto;

import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportRequest {
    @NotNull(message = "Report ID cannot be null")
    private Long reportId;
    
    @NotNull(message = "Reviewer ID cannot be null")
    private Long reviewerId;
    
    @NotNull(message = "Status cannot be null")
    private ReportStatus status;
    
    private String moderatorNotes;
}
