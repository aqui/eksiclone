package in.batur.eksiclone.moderationservice.service.impl;

import in.batur.eksiclone.entity.moderation.ModeratorAction;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.moderationservice.dto.CreateModeratorActionRequest;
import in.batur.eksiclone.moderationservice.dto.ModeratorActionDTO;
import in.batur.eksiclone.moderationservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.moderationservice.mapper.ModeratorActionMapper;
import in.batur.eksiclone.moderationservice.service.ModeratorActionService;
import in.batur.eksiclone.repository.moderation.ModeratorActionRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModeratorActionServiceImpl implements ModeratorActionService {

    private final ModeratorActionRepository moderatorActionRepository;
    private final UserRepository userRepository;
    private final ModeratorActionMapper moderatorActionMapper;

    public ModeratorActionServiceImpl(
            ModeratorActionRepository moderatorActionRepository,
            UserRepository userRepository,
            ModeratorActionMapper moderatorActionMapper) {
        this.moderatorActionRepository = moderatorActionRepository;
        this.userRepository = userRepository;
        this.moderatorActionMapper = moderatorActionMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"moderatorActions"}, allEntries = true)
    public ModeratorActionDTO createAction(CreateModeratorActionRequest request) {
        User moderator = findUserById(request.getModeratorId());
        
        ModeratorAction action = new ModeratorAction();
        action.setActionType(request.getActionType());
        action.setModerator(moderator);
        action.setContentType(request.getContentType());
        action.setContentId(request.getContentId());
        
        if (request.getTargetUserId() != null) {
            User targetUser = findUserById(request.getTargetUserId());
            action.setTargetUser(targetUser);
        }
        
        action.setAffectedReportId(request.getAffectedReportId());
        action.setReason(request.getReason());
        action.setDetails(request.getDetails());
        
        action = moderatorActionRepository.save(action);
        
        return moderatorActionMapper.toDto(action);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private ModeratorAction findActionById(Long id) {
        return moderatorActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator action not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'action:' + #id")
    public ModeratorActionDTO getAction(Long id) {
        return moderatorActionMapper.toDto(findActionById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'moderator:' + #moderatorId + ':' + #pageable")
    public Page<ModeratorActionDTO> getActionsByModerator(Long moderatorId, Pageable pageable) {
        User moderator = findUserById(moderatorId);
        return moderatorActionRepository.findByModerator(moderator, pageable)
                .map(moderatorActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'targetUser:' + #targetUserId + ':' + #pageable")
    public Page<ModeratorActionDTO> getActionsByTargetUser(Long targetUserId, Pageable pageable) {
        User targetUser = findUserById(targetUserId);
        return moderatorActionRepository.findByTargetUser(targetUser, pageable)
                .map(moderatorActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'content:' + #contentType + ':' + #contentId + ':' + #pageable")
    public Page<ModeratorActionDTO> getActionsByContent(ContentType contentType, Long contentId, Pageable pageable) {
        return moderatorActionRepository.findByContentTypeAndContentId(contentType, contentId, pageable)
                .map(moderatorActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'type:' + #actionType + ':' + #pageable")
    public Page<ModeratorActionDTO> getActionsByType(ActionType actionType, Pageable pageable) {
        return moderatorActionRepository.findByActionType(actionType, pageable)
                .map(moderatorActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'dateRange:' + #startDate + ':' + #endDate + ':' + #pageable")
    public Page<ModeratorActionDTO> getActionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return moderatorActionRepository.findByDateRange(startDate, endDate, pageable)
                .map(moderatorActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'actionTypeCount:' + #startDate + ':' + #endDate")
    public Map<String, Long> countActionsByTypeInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = moderatorActionRepository.countByActionTypeInDateRange(startDate, endDate);
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            ActionType type = (ActionType) result[0];
            Long count = (Long) result[1];
            counts.put(type.name(), count);
        }
        
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moderatorActions", key = "'actionModeratorCount'")
    public Map<String, Long> countActionsByModerator() {
        List<Object[]> results = moderatorActionRepository.countByModerator();
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            Long moderatorId = (Long) result[0];
            Long count = (Long) result[1];
            
            try {
                User moderator = findUserById(moderatorId);
                counts.put(moderator.getUsername(), count);
            } catch (ResourceNotFoundException e) {
                counts.put("Unknown (ID: " + moderatorId + ")", count);
            }
        }
        
        return counts;
    }
}
