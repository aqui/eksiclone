package in.batur.eksiclone.notificationservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private String notificationType;
    private Long resourceId;
    private boolean isRead;
    private LocalDateTime createdDate;
}
