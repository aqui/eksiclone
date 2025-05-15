package in.batur.eksiclone.notificationservice.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import in.batur.eksiclone.notificationservice.dto.CreateNotificationRequest;
import in.batur.eksiclone.notificationservice.dto.NotificationDTO;

public interface NotificationService {
    NotificationDTO createNotification(CreateNotificationRequest request);
    
    NotificationDTO getNotification(Long id);
    
    Page<NotificationDTO> getNotificationsByUser(Long userId, Pageable pageable);
    
    List<NotificationDTO> getUnreadNotificationsByUser(Long userId);
    
    Page<NotificationDTO> getUnreadNotificationsByUserPaged(Long userId, Pageable pageable);
    
    NotificationDTO markAsRead(Long id);
    
    void markAllAsRead(Long userId);
    
    void deleteNotification(Long id);
    
    void deleteAllNotifications(Long userId);
    
    Long countUnreadNotifications(Long userId);
}
