package in.batur.eksiclone.repository.moderation;

import in.batur.eksiclone.entity.moderation.UserModeration;
import in.batur.eksiclone.entity.moderation.UserModeration.ModerationType;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserModerationRepository extends JpaRepository<UserModeration, Long>, BaseRepository<UserModeration> {
    
    Page<UserModeration> findByUser(User user, Pageable pageable);
    
    Page<UserModeration> findByModerator(User moderator, Pageable pageable);
    
    Page<UserModeration> findByUserAndActive(User user, boolean active, Pageable pageable);
    
    Page<UserModeration> findByType(ModerationType type, Pageable pageable);
    
    @Query("SELECT um FROM UserModeration um WHERE um.user = :user AND um.active = true AND um.type = :type")
    Optional<UserModeration> findActiveByUserAndType(@Param("user") User user, @Param("type") ModerationType type);
    
    @Query("SELECT CASE WHEN COUNT(um) > 0 THEN true ELSE false END FROM UserModeration um WHERE um.user = :user AND um.active = true AND um.type = :type")
    boolean existsActiveByUserAndType(@Param("user") User user, @Param("type") ModerationType type);
    
    @Query("SELECT um FROM UserModeration um WHERE um.active = true AND um.expiryDate IS NOT NULL AND um.expiryDate < :now")
    List<UserModeration> findExpiredModerations(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(um) FROM UserModeration um WHERE um.active = true")
    long countActiveModeration();
    
    @Query("SELECT um.type, COUNT(um) FROM UserModeration um WHERE um.active = true GROUP BY um.type")
    List<Object[]> countActiveByType();
}
