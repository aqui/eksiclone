package in.batur.eksiclone.notificationservice.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import in.batur.eksiclone.notificationservice.dto.CreateNotificationRequest;
import in.batur.eksiclone.notificationservice.dto.NotificationDTO;
import in.batur.eksiclone.notificationservice.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public NotificationDTO createNotification(CreateNotificationRequest request) {
        // Mock implementation
        if (request.getUserId() == null || request.getMessage() == null || request.getNotificationType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID, message, and notification type are required");
        }
        
        // Mock notification creation
        return NotificationDTO.builder()
                .id(1L)
                .userId(request.getUserId())
                .message(request.getMessage())
                .notificationType(request.getNotificationType())
                .resourceId(request.getResourceId())
                .isRead(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Override
    public NotificationDTO getNotification(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification ID");
        }
        
        // Mock notification retrieval
        return NotificationDTO.builder()
                .id(id)
                .userId(100L)
                .message("You have a new mention")
                .notificationType("MENTION")
                .resourceId(200L)
                .isRead(false)
                .createdDate(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Override
    public Page<NotificationDTO> getNotificationsByUser(Long userId, Pageable pageable) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Generate mock data
        List<NotificationDTO> notifications = generateMockNotifications(userId, 10);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), notifications.size());
        
        return new PageImpl<>(
                notifications.subList(start, end),
                pageable,
                notifications.size());
    }

    @Override
    public List<NotificationDTO> getUnreadNotificationsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Generate mock data and filter for unread
        List<NotificationDTO> notifications = generateMockNotifications(userId, 10);
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDTO> getUnreadNotificationsByUserPaged(Long userId, Pageable pageable) {
        List<NotificationDTO> unreadNotifications = getUnreadNotificationsByUser(userId);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), unreadNotifications.size());
        
        return new PageImpl<>(
                unreadNotifications.subList(start, end),
                pageable,
                unreadNotifications.size());
    }

    @Override
    public NotificationDTO markAsRead(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification ID");
        }
        
        // Mock update (in a real implementation, we would find and update the notification)
        NotificationDTO notification = getNotification(id);
        notification.setRead(true);
        
        return notification;
    }

    @Override
    public void markAllAsRead(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // In a real implementation, we would update all unread notifications for the user
    }

    @Override
    public void deleteNotification(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification ID");
        }
        
        // In a real implementation, we would find and delete the notification
    }

    @Override
    public void deleteAllNotifications(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // In a real implementation, we would delete all notifications for the user
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Mock count (in a real implementation, this would query the database)
        return 5L;
    }
    
    // Helper method to generate mock notifications
    private List<NotificationDTO> generateMockNotifications(Long userId, int count) {
        List<NotificationDTO> notifications = new ArrayList<>();
        String[] types = {"MENTION", "REPLY", "LIKE", "FAVORITE", "SYSTEM"};
        
        for (int i = 1; i <= count; i++) {
            String type = types[i % types.length];
            boolean isRead = i % 2 == 0; // Even indices are read
            
            notifications.add(NotificationDTO.builder()
                    .id((long) i)
                    .userId(userId)
                    .message("Notification message " + i + " of type " + type)
                    .notificationType(type)
                    .resourceId((long) (i * 100))
                    .isRead(isRead)
                    .createdDate(LocalDateTime.now().minusHours(i))
                    .build());
        }
        
        return notifications;
    }
}
