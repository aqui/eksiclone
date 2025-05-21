package in.batur.eksiclone.moderationservice.dto;

import in.batur.eksiclone.entity.moderation.Report.ReportReason;
import in.batur.eksiclone.entity.moderation.Report.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    @NotNull(message = "Content type cannot be null")
    private ReportType contentType;
    
    @NotNull(message = "Content ID cannot be null")
    private Long contentId;
    
    @NotNull(message = "Reporter ID cannot be null")
    private Long reporterId;
    
    @NotNull(message = "Reason cannot be null")
    private ReportReason reason;
    
    private String description;
}
