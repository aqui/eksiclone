package in.batur.eksiclone.moderationservice.dto;

import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateModeratorActionRequest {
    @NotNull(message = "Action type cannot be null")
    private ActionType actionType;
    
    @NotNull(message = "Moderator ID cannot be null")
    private Long moderatorId;
    
    @NotNull(message = "Content type cannot be null")
    private ContentType contentType;
    
    @NotNull(message = "Content ID cannot be null")
    private Long contentId;
    
    private Long targetUserId;
    
    private Long affectedReportId;
    
    @NotNull(message = "Reason cannot be null")
    private String reason;
    
    private String details;
}
