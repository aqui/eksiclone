package in.batur.eksiclone.moderationservice.service.impl;

import in.batur.eksiclone.entity.moderation.UserModeration;
import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.moderationservice.dto.CreateUserModerationRequest;
import in.batur.eksiclone.moderationservice.dto.UserModerationDTO;
import in.batur.eksiclone.moderationservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.moderationservice.mapper.UserModerationMapper;
import in.batur.eksiclone.moderationservice.service.UserModerationService;
import in.batur.eksiclone.repository.moderation.UserModerationRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserModerationServiceImpl implements UserModerationService {

    private final UserModerationRepository userModerationRepository;
    private final UserRepository userRepository;
    private final UserModerationMapper userModerationMapper;

    public UserModerationServiceImpl(
            UserModerationRepository userModerationRepository,
            UserRepository userRepository,
            UserModerationMapper userModerationMapper) {
        this.userModerationRepository = userModerationRepository;
        this.userRepository = userRepository;
        this.userModerationMapper = userModerationMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"userModerations"}, allEntries = true)
    public UserModerationDTO createModeration(CreateUserModerationRequest request) {
        User user = findUserById(request.getUserId());
        User moderator = findUserById(request.getModeratorId());
        
        // Check if user already has active moderation of this type
        if (request.getType() == ModerationType.BAN || request.getType() == ModerationType.SUSPENSION) {
            Optional<UserModeration> existingModeration = userModerationRepository.findActiveByUserAndType(user, request.getType());
            if (existingModeration.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "User already has an active " + request.getType().name().toLowerCase());
            }
        }
        
        UserModeration moderation = new UserModeration();
        moderation.setUser(user);
        moderation.setType(request.getType());
        moderation.setModerator(moderator);
        moderation.setExpiryDate(request.getExpiryDate());
        moderation.setReason(request.getReason());
        moderation.setNotes(request.getNotes());
        moderation.setActive(true);
        
        moderation = userModerationRepository.save(moderation);
        
        return userModerationMapper.toDto(moderation);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private UserModeration findModerationById(Long id) {
        return userModerationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User moderation not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'moderation:' + #id")
    public UserModerationDTO getModeration(Long id) {
        return userModerationMapper.toDto(findModerationById(id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"userModerations"}, allEntries = true)
    public UserModerationDTO deactivateModeration(Long id) {
        UserModeration moderation = findModerationById(id);
        
        if (!moderation.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moderation is already inactive");
        }
        
        moderation.setActive(false);
        moderation = userModerationRepository.save(moderation);
        
        return userModerationMapper.toDto(moderation);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'user:' + #userId + ':' + #pageable")
    public Page<UserModerationDTO> getUserModerations(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return userModerationRepository.findByUser(user, pageable)
                .map(userModerationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'userActive:' + #userId + ':' + #pageable")
    public Page<UserModerationDTO> getActiveUserModerations(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return userModerationRepository.findByUserAndActive(user, true, pageable)
                .map(userModerationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'moderator:' + #moderatorId + ':' + #pageable")
    public Page<UserModerationDTO> getModerationsByModerator(Long moderatorId, Pageable pageable) {
        User moderator = findUserById(moderatorId);
        return userModerationRepository.findByModerator(moderator, pageable)
                .map(userModerationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'type:' + #type + ':' + #pageable")
    public Page<UserModerationDTO> getModerationsByType(ModerationType type, Pageable pageable) {
        return userModerationRepository.findByType(type, pageable)
                .map(userModerationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'hasActive:' + #userId + ':' + #type")
    public boolean hasActiveModeration(Long userId, ModerationType type) {
        User user = findUserById(userId);
        return userModerationRepository.existsActiveByUserAndType(user, type);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'countActive'")
    public long countActiveModerations() {
        return userModerationRepository.countActiveModeration();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userModerations", key = "'countActiveByType'")
    public Map<String, Long> countActiveByType() {
        List<Object[]> results = userModerationRepository.countActiveByType();
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            ModerationType type = (ModerationType) result[0];
            Long count = (Long) result[1];
            counts.put(type.name(), count);
        }
        
        return counts;
    }
}
