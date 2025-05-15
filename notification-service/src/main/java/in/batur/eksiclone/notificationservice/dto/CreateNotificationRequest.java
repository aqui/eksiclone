package in.batur.eksiclone.notificationservice.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotBlank(message = "Notification type is required")
    private String notificationType;
    
    private Long resourceId;
}
