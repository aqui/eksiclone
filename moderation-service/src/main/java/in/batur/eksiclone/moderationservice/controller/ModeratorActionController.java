package in.batur.eksiclone.moderationservice.controller;

import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;
import in.batur.eksiclone.moderationservice.dto.ApiResponse;
import in.batur.eksiclone.moderationservice.dto.CreateModeratorActionRequest;
import in.batur.eksiclone.moderationservice.dto.ModeratorActionDTO;
import in.batur.eksiclone.moderationservice.service.ModeratorActionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/moderation/actions")
public class ModeratorActionController {

    private final ModeratorActionService moderatorActionService;

    public ModeratorActionController(ModeratorActionService moderatorActionService) {
        this.moderatorActionService = moderatorActionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ModeratorActionDTO>> createAction(@RequestBody @Validated CreateModeratorActionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(moderatorActionService.createAction(request), "Moderator action created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeratorActionDTO>> getAction(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(moderatorActionService.getAction(id), "Moderator action retrieved successfully"));
    }

    @GetMapping("/moderator/{moderatorId}")
    public ResponseEntity<ApiResponse<Page<ModeratorActionDTO>>> getActionsByModerator(
            @PathVariable Long moderatorId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.getActionsByModerator(moderatorId, pageable),
                "Moderator actions retrieved by moderator"));
    }

    @GetMapping("/target-user/{targetUserId}")
    public ResponseEntity<ApiResponse<Page<ModeratorActionDTO>>> getActionsByTargetUser(
            @PathVariable Long targetUserId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.getActionsByTargetUser(targetUserId, pageable),
                "Moderator actions retrieved by target user"));
    }

    @GetMapping("/content/{contentType}/{contentId}")
    public ResponseEntity<ApiResponse<Page<ModeratorActionDTO>>> getActionsByContent(
            @PathVariable ContentType contentType,
            @PathVariable Long contentId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.getActionsByContent(contentType, contentId, pageable),
                "Moderator actions retrieved by content"));
    }

    @GetMapping("/type/{actionType}")
    public ResponseEntity<ApiResponse<Page<ModeratorActionDTO>>> getActionsByType(
            @PathVariable ActionType actionType,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.getActionsByType(actionType, pageable),
                "Moderator actions retrieved by type"));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<ModeratorActionDTO>>> getActionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.getActionsByDateRange(startDate, endDate, pageable),
                "Moderator actions retrieved by date range"));
    }

    @GetMapping("/stats/by-type")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countActionsByTypeInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.countActionsByTypeInDateRange(startDate, endDate),
                "Action counts by type retrieved"));
    }

    @GetMapping("/stats/by-moderator")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countActionsByModerator() {
        return ResponseEntity.ok(new ApiResponse<>(
                moderatorActionService.countActionsByModerator(),
                "Action counts by moderator retrieved"));
    }
}
