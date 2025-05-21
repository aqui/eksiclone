package in.batur.eksiclone.moderationservice.mapper;

import in.batur.eksiclone.entity.moderation.UserModeration;
import in.batur.eksiclone.moderationservice.dto.UserModerationDTO;

import org.springframework.stereotype.Component;

@Component
public class UserModerationMapper {

    public UserModerationDTO toDto(UserModeration moderation) {
        if (moderation == null) {
            return null;
        }
        
        return UserModerationDTO.builder()
                .id(moderation.getId())
                .userId(moderation.getUser().getId())
                .username(moderation.getUser().getUsername())
                .type(moderation.getType())
                .moderatorId(moderation.getModerator().getId())
                .moderatorUsername(moderation.getModerator().getUsername())
                .expiryDate(moderation.getExpiryDate())
                .active(moderation.isActive())
                .reason(moderation.getReason())
                .notes(moderation.getNotes())
                .createdDate(moderation.getCreatedDate())
                .lastUpdatedDate(moderation.getLastUpdatedDate())
                .build();
    }
}
