package in.batur.eksiclone.repository.moderation;

import in.batur.eksiclone.entity.moderation.ModeratorAction;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ActionType;
import in.batur.eksiclone.entity.moderation.ModeratorAction.ContentType;
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

@Repository
@Transactional(readOnly = true)
public interface ModeratorActionRepository extends JpaRepository<ModeratorAction, Long>, BaseRepository<ModeratorAction> {
    
    Page<ModeratorAction> findByModerator(User moderator, Pageable pageable);
    
    Page<ModeratorAction> findByTargetUser(User targetUser, Pageable pageable);
    
    Page<ModeratorAction> findByContentTypeAndContentId(ContentType contentType, Long contentId, Pageable pageable);
    
    Page<ModeratorAction> findByActionType(ActionType actionType, Pageable pageable);
    
    @Query("SELECT ma FROM ModeratorAction ma WHERE ma.createdDate BETWEEN :startDate AND :endDate")
    Page<ModeratorAction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    @Query("SELECT ma.actionType, COUNT(ma) FROM ModeratorAction ma WHERE ma.createdDate BETWEEN :startDate AND :endDate GROUP BY ma.actionType")
    List<Object[]> countByActionTypeInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ma.moderator.id, COUNT(ma) FROM ModeratorAction ma GROUP BY ma.moderator.id")
    List<Object[]> countByModerator();
}
