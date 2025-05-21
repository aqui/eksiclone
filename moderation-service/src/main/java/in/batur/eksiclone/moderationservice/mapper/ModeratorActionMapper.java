package in.batur.eksiclone.moderationservice.mapper;

import in.batur.eksiclone.entity.moderation.ModeratorAction;
import in.batur.eksiclone.moderationservice.dto.ModeratorActionDTO;

import org.springframework.stereotype.Component;

@Component
public class ModeratorActionMapper {

    public ModeratorActionDTO toDto(ModeratorAction action) {
        if (action == null) {
            return null;
        }
        
        ModeratorActionDTO.ModeratorActionDTOBuilder builder = ModeratorActionDTO.builder()
                .id(action.getId())
                .actionType(action.getActionType())
                .moderatorId(action.getModerator().getId())
                .moderatorUsername(action.getModerator().getUsername())
                .contentType(action.getContentType())
                .contentId(action.getContentId())
                .affectedReportId(action.getAffectedReportId())
                .reason(action.getReason())
                .details(action.getDetails())
                .createdDate(action.getCreatedDate())
                .lastUpdatedDate(action.getLastUpdatedDate());
        
        if (action.getTargetUser() != null) {
            builder.targetUserId(action.getTargetUser().getId())
                   .targetUsername(action.getTargetUser().getUsername());
        }
        
        return builder.build();
    }
}
