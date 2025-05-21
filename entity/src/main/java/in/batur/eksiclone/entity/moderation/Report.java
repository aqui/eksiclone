package in.batur.eksiclone.entity.moderation;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report extends BaseEntity {
    
    public enum ReportType {
        ENTRY, TOPIC, USER, MESSAGE
    }
    
    public enum ReportReason {
        INAPPROPRIATE_CONTENT,
        HATE_SPEECH,
        HARASSMENT,
        SPAM,
        VIOLENCE,
        COPYRIGHT_VIOLATION,
        OTHER
    }
    
    public enum ReportStatus {
        PENDING,
        REVIEWED,
        ACCEPTED,
        REJECTED
    }
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType contentType;
    
    @Column(name = "content_id", nullable = false)
    private Long contentId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
    
    @Column(name = "review_date")
    private LocalDateTime reviewDate;
    
    @Column(name = "moderator_notes", columnDefinition = "TEXT")
    private String moderatorNotes;
}
