package in.batur.eksiclone.moderationservice.controller;

import in.batur.eksiclone.moderationservice.dto.ApiResponse;
import in.batur.eksiclone.moderationservice.dto.ModerationStatsDTO;
import in.batur.eksiclone.moderationservice.service.ModerationStatsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moderation/stats")
public class ModerationStatsController {

    private final ModerationStatsService moderationStatsService;

    public ModerationStatsController(ModerationStatsService moderationStatsService) {
        this.moderationStatsService = moderationStatsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ModerationStatsDTO>> getStats() {
        return ResponseEntity.ok(new ApiResponse<>(
                moderationStatsService.getStats(),
                "Moderation statistics retrieved successfully"));
    }
}
