package in.batur.eksiclone.moderationservice.dto;

import java.time.LocalDateTime;

import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModeratorActionDTO {
    private Long id;
    private ActionType actionType;
    private Long moderatorId;
    private String moderatorUsername;
    private ContentType contentType;
    private Long contentId;
    private Long targetUserId;
    private String targetUsername;
    private Long affectedReportId;
    private String reason;
    private String details;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}
