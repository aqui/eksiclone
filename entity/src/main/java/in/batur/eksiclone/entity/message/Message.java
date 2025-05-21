package in.batur.eksiclone.entity.message;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_read")
    private boolean isRead = false;
    
    @Column(name = "is_deleted_by_sender")
    private boolean isDeletedBySender = false;
    
    @Column(name = "is_deleted_by_receiver")
    private boolean isDeletedByReceiver = false;
}
