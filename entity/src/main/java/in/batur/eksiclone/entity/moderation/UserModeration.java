package in.batur.eksiclone.entity.moderation;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_moderations")
@Getter
@Setter
public class UserModeration extends BaseEntity {
    
    public enum ModerationType {
        WARNING,
        SUSPENSION,
        BAN
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationType type;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}
