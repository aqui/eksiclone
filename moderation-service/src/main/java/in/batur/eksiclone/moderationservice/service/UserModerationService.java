package in.batur.eksiclone.moderationservice.service;

import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;
import in.batur.eksiclone.moderationservice.dto.CreateUserModerationRequest;
import in.batur.eksiclone.moderationservice.dto.UserModerationDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserModerationService {
    UserModerationDTO createModeration(CreateUserModerationRequest request);
    
    UserModerationDTO getModeration(Long id);
    
    UserModerationDTO deactivateModeration(Long id);
    
    Page<UserModerationDTO> getUserModerations(Long userId, Pageable pageable);
    
    Page<UserModerationDTO> getActiveUserModerations(Long userId, Pageable pageable);
    
    Page<UserModerationDTO> getModerationsByModerator(Long moderatorId, Pageable pageable);
    
    Page<UserModerationDTO> getModerationsByType(ModerationType type, Pageable pageable);
    
    boolean hasActiveModeration(Long userId, ModerationType type);
    
    long countActiveModerations();
    
    Map<String, Long> countActiveByType();
}
