package in.batur.eksiclone.moderationservice.mapper;

import in.batur.eksiclone.entity.moderation.Report;
import in.batur.eksiclone.moderationservice.dto.ReportDTO;

import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportDTO toDto(Report report) {
        if (report == null) {
            return null;
        }
        
        ReportDTO.ReportDTOBuilder builder = ReportDTO.builder()
                .id(report.getId())
                .contentType(report.getContentType())
                .contentId(report.getContentId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdDate(report.getCreatedDate())
                .lastUpdatedDate(report.getLastUpdatedDate());
        
        if (report.getReviewer() != null) {
            builder.reviewerId(report.getReviewer().getId())
                   .reviewerUsername(report.getReviewer().getUsername());
        }
        
        builder.reviewDate(report.getReviewDate())
               .moderatorNotes(report.getModeratorNotes());
        
        return builder.build();
    }
}
