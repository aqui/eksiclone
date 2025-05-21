package in.batur.eksiclone.moderationservice.dto;

import java.time.LocalDateTime;

import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModerationDTO {
    private Long id;
    private Long userId;
    private String username;
    private ModerationType type;
    private Long moderatorId;
    private String moderatorUsername;
    private LocalDateTime expiryDate;
    private boolean active;
    private String reason;
    private String notes;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}
