package in.batur.eksiclone.moderationservice.service;

import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.entity.moderation.Report.ReportType;
import in.batur.eksiclone.moderationservice.dto.CreateReportRequest;
import in.batur.eksiclone.moderationservice.dto.ReportDTO;
import in.batur.eksiclone.moderationservice.dto.ReviewReportRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    ReportDTO createReport(CreateReportRequest request);
    
    ReportDTO getReport(Long id);
    
    ReportDTO reviewReport(ReviewReportRequest request);
    
    Page<ReportDTO> getReportsByStatus(ReportStatus status, Pageable pageable);
    
    Page<ReportDTO> getReportsByReporter(Long reporterId, Pageable pageable);
    
    Page<ReportDTO> getReportsByReviewer(Long reviewerId, Pageable pageable);
    
    Page<ReportDTO> getReportsByContent(ReportType contentType, Long contentId, Pageable pageable);
    
    List<ReportDTO> getActiveReportsByContent(ReportType contentType, Long contentId);
    
    long countReportsByStatus(ReportStatus status);
}
