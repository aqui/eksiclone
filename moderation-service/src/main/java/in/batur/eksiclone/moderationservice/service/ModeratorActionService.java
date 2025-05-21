package in.batur.eksiclone.moderationservice.service;

import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;
import in.batur.eksiclone.moderationservice.dto.CreateModeratorActionRequest;
import in.batur.eksiclone.moderationservice.dto.ModeratorActionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface ModeratorActionService {
    ModeratorActionDTO createAction(CreateModeratorActionRequest request);
    
    ModeratorActionDTO getAction(Long id);
    
    Page<ModeratorActionDTO> getActionsByModerator(Long moderatorId, Pageable pageable);
    
    Page<ModeratorActionDTO> getActionsByTargetUser(Long targetUserId, Pageable pageable);
    
    Page<ModeratorActionDTO> getActionsByContent(ContentType contentType, Long contentId, Pageable pageable);
    
    Page<ModeratorActionDTO> getActionsByType(ActionType actionType, Pageable pageable);
    
    Page<ModeratorActionDTO> getActionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Map<String, Long> countActionsByTypeInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Long> countActionsByModerator();
}
