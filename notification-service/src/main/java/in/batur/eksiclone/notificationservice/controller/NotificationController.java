package in.batur.eksiclone.notificationservice.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import in.batur.eksiclone.notificationservice.dto.ApiResponse;
import in.batur.eksiclone.notificationservice.dto.CreateNotificationRequest;
import in.batur.eksiclone.notificationservice.dto.NotificationDTO;
import in.batur.eksiclone.notificationservice.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDTO>> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
        NotificationDTO notification = notificationService.createNotification(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(notification, "Notification created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationDTO>> getNotification(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotification(id);
        return ResponseEntity.ok(new ApiResponse<>(notification, "Notification retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getNotificationsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NotificationDTO> notifications = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(notifications, "Notifications retrieved successfully"));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotificationsByUser(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(notifications, "Unread notifications retrieved successfully"));
    }

    @GetMapping("/user/{userId}/unread/paged")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getUnreadNotificationsByUserPaged(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NotificationDTO> notifications = notificationService.getUnreadNotificationsByUserPaged(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(notifications, "Unread notifications retrieved successfully"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(@PathVariable Long id) {
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>(notification, "Notification marked as read"));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(new ApiResponse<>(null, "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Notification deleted successfully"));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(null, "All notifications deleted successfully"));
    }

    @GetMapping("/user/{userId}/count-unread")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(count, "Unread notification count retrieved successfully"));
    }
}
