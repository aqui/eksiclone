package in.batur.eksiclone.moderationservice.controller;

import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;
import in.batur.eksiclone.moderationservice.dto.ApiResponse;
import in.batur.eksiclone.moderationservice.dto.CreateUserModerationRequest;
import in.batur.eksiclone.moderationservice.dto.UserModerationDTO;
import in.batur.eksiclone.moderationservice.service.UserModerationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/moderation/user-moderations")
public class UserModerationController {

    private final UserModerationService userModerationService;

    public UserModerationController(UserModerationService userModerationService) {
        this.userModerationService = userModerationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserModerationDTO>> createModeration(@RequestBody @Validated CreateUserModerationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userModerationService.createModeration(request), "User moderation created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserModerationDTO>> getModeration(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(userModerationService.getModeration(id), "User moderation retrieved successfully"));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserModerationDTO>> deactivateModeration(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(userModerationService.deactivateModeration(id), "User moderation deactivated successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<UserModerationDTO>>> getUserModerations(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.getUserModerations(userId, pageable),
                "User moderations retrieved"));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<Page<UserModerationDTO>>> getActiveUserModerations(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.getActiveUserModerations(userId, pageable),
                "Active user moderations retrieved"));
    }

    @GetMapping("/moderator/{moderatorId}")
    public ResponseEntity<ApiResponse<Page<UserModerationDTO>>> getModerationsByModerator(
            @PathVariable Long moderatorId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.getModerationsByModerator(moderatorId, pageable),
                "User moderations by moderator retrieved"));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<UserModerationDTO>>> getModerationsByType(
            @PathVariable ModerationType type,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.getModerationsByType(type, pageable),
                "User moderations by type retrieved"));
    }

    @GetMapping("/check/{userId}/{type}")
    public ResponseEntity<ApiResponse<Boolean>> hasActiveModeration(
            @PathVariable Long userId,
            @PathVariable ModerationType type) {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.hasActiveModeration(userId, type),
                "Active moderation check completed"));
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<ApiResponse<Long>> countActiveModerations() {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.countActiveModerations(),
                "Active moderation count retrieved"));
    }

    @GetMapping("/stats/by-type")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countActiveByType() {
        return ResponseEntity.ok(new ApiResponse<>(
                userModerationService.countActiveByType(),
                "Active moderation counts by type retrieved"));
    }
}
