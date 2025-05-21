package in.batur.eksiclone.moderationservice.service.impl;

import in.batur.eksiclone.entity.moderation.Report;
import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.entity.moderation.Report.ReportType;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.moderationservice.dto.CreateReportRequest;
import in.batur.eksiclone.moderationservice.dto.ReportDTO;
import in.batur.eksiclone.moderationservice.dto.ReviewReportRequest;
import in.batur.eksiclone.moderationservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.moderationservice.mapper.ReportMapper;
import in.batur.eksiclone.moderationservice.service.ReportService;
import in.batur.eksiclone.repository.moderation.ReportRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            UserRepository userRepository,
            ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"reports"}, allEntries = true)
    public ReportDTO createReport(CreateReportRequest request) {
        User reporter = findUserById(request.getReporterId());
        
        Report report = new Report();
        report.setContentType(request.getContentType());
        report.setContentId(request.getContentId());
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);
        
        report = reportRepository.save(report);
        
        return reportMapper.toDto(report);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private Report findReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'report:' + #id")
    public ReportDTO getReport(Long id) {
        return reportMapper.toDto(findReportById(id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"reports"}, allEntries = true)
    public ReportDTO reviewReport(ReviewReportRequest request) {
        Report report = findReportById(request.getReportId());
        
        // Check if report is already reviewed
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report has already been reviewed");
        }
        
        User reviewer = findUserById(request.getReviewerId());
        
        report.setStatus(request.getStatus());
        report.setReviewer(reviewer);
        report.setReviewDate(LocalDateTime.now());
        report.setModeratorNotes(request.getModeratorNotes());
        
        report = reportRepository.save(report);
        
        return reportMapper.toDto(report);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'status:' + #status + ':' + #pageable")
    public Page<ReportDTO> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable)
                .map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'reporter:' + #reporterId + ':' + #pageable")
    public Page<ReportDTO> getReportsByReporter(Long reporterId, Pageable pageable) {
        User reporter = findUserById(reporterId);
        return reportRepository.findByReporter(reporter, pageable)
                .map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'reviewer:' + #reviewerId + ':' + #pageable")
    public Page<ReportDTO> getReportsByReviewer(Long reviewerId, Pageable pageable) {
        User reviewer = findUserById(reviewerId);
        return reportRepository.findByReviewer(reviewer, pageable)
                .map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'content:' + #contentType + ':' + #contentId + ':' + #pageable")
    public Page<ReportDTO> getReportsByContent(ReportType contentType, Long contentId, Pageable pageable) {
        return reportRepository.findByContentTypeAndContentId(contentType, contentId, pageable)
                .map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'activeContent:' + #contentType + ':' + #contentId")
    public List<ReportDTO> getActiveReportsByContent(ReportType contentType, Long contentId) {
        return reportRepository.findByContentTypeAndContentIdAndStatus(contentType, contentId, ReportStatus.PENDING)
                .stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reports", key = "'countStatus:' + #status")
    public long countReportsByStatus(ReportStatus status) {
        return reportRepository.countByStatus(status);
    }
}
