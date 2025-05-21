package in.batur.eksiclone.moderationservice.scheduled;

import in.batur.eksiclone.entity.moderation.UserModeration;
import in.batur.eksiclone.repository.moderation.UserModerationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ModerationScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ModerationScheduledTasks.class);
    
    private final UserModerationRepository userModerationRepository;
    
    public ModerationScheduledTasks(UserModerationRepository userModerationRepository) {
        this.userModerationRepository = userModerationRepository;
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void processExpiredModerations() {
        LocalDateTime now = LocalDateTime.now();
        List<UserModeration> expiredModerations = userModerationRepository.findExpiredModerations(now);
        
        if (!expiredModerations.isEmpty()) {
            logger.info("Processing {} expired user moderations", expiredModerations.size());
            
            for (UserModeration moderation : expiredModerations) {
                moderation.setActive(false);
                logger.info("Deactivated expired moderation for user ID {}, type {}", 
                        moderation.getUser().getId(), moderation.getType());
            }
            
            userModerationRepository.saveAll(expiredModerations);
            logger.info("Completed processing of expired moderations");
        }
    }
}
