package in.batur.eksiclone.moderationservice.dto;

import java.time.LocalDateTime;

import in.batur.eksiclone.entity.moderation.Report.ReportReason;
import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.entity.moderation.Report.ReportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private ReportType contentType;
    private Long contentId;
    private Long reporterId;
    private String reporterUsername;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private Long reviewerId;
    private String reviewerUsername;
    private LocalDateTime reviewDate;
    private String moderatorNotes;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}
