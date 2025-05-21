package in.batur.eksiclone.entity.moderation;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "moderator_actions")
@Getter
@Setter
public class ModeratorAction extends BaseEntity {
    
    public enum ActionType {
        CONTENT_REMOVE,
        CONTENT_EDIT,
        USER_WARN,
        USER_SUSPEND,
        USER_BAN,
        REPORT_PROCESS
    }
    
    public enum ContentType {
        ENTRY, TOPIC, USER, MESSAGE
    }
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    
    @Column(name = "content_id", nullable = false)
    private Long contentId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    private User targetUser;
    
    @Column(name = "affected_report_id")
    private Long affectedReportId;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String details;
}