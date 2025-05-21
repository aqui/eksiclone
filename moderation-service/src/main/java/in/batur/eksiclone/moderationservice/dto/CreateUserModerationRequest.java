package in.batur.eksiclone.moderationservice.dto;

import java.time.LocalDateTime;

import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserModerationRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotNull(message = "Moderation type cannot be null")
    private ModerationType type;
    
    @NotNull(message = "Moderator ID cannot be null")
    private Long moderatorId;
    
    private LocalDateTime expiryDate;
    
    @NotNull(message = "Reason cannot be null")
    private String reason;
    
    private String notes;
}
