package in.batur.eksiclone.moderationservice.controller;

import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.entity.moderation.Report.ReportType;
import in.batur.eksiclone.moderationservice.dto.ApiResponse;
import in.batur.eksiclone.moderationservice.dto.CreateReportRequest;
import in.batur.eksiclone.moderationservice.dto.ReportDTO;
import in.batur.eksiclone.moderationservice.dto.ReviewReportRequest;
import in.batur.eksiclone.moderationservice.service.ReportService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moderation/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(@RequestBody @Validated CreateReportRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(reportService.createReport(request), "Report created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(reportService.getReport(id), "Report retrieved successfully"));
    }

    @PostMapping("/review")
    public ResponseEntity<ApiResponse<ReportDTO>> reviewReport(@RequestBody @Validated ReviewReportRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(reportService.reviewReport(request), "Report reviewed successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByStatus(
            @PathVariable ReportStatus status,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.getReportsByStatus(status, pageable),
                "Reports retrieved by status"));
    }

    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByReporter(
            @PathVariable Long reporterId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.getReportsByReporter(reporterId, pageable),
                "Reports retrieved by reporter"));
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByReviewer(
            @PathVariable Long reviewerId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.getReportsByReviewer(reviewerId, pageable),
                "Reports retrieved by reviewer"));
    }

    @GetMapping("/content/{contentType}/{contentId}")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByContent(
            @PathVariable ReportType contentType,
            @PathVariable Long contentId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.getReportsByContent(contentType, contentId, pageable),
                "Reports retrieved by content"));
    }

    @GetMapping("/active-content/{contentType}/{contentId}")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getActiveReportsByContent(
            @PathVariable ReportType contentType,
            @PathVariable Long contentId) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.getActiveReportsByContent(contentType, contentId),
                "Active reports retrieved by content"));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(
                reportService.countReportsByStatus(status),
                "Report count retrieved by status"));
    }
}
