package in.batur.eksiclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "entries")
@Getter
@Setter
@NoArgsConstructor
public class Entry extends BaseEntity {
    
    @Column(nullable = false, length = 10000)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Column(name = "favorite_count")
    private Long favoriteCount = 0L;
    
    @Column(name = "is_edited")
    private boolean edited = false;
    
    @Version
    private Long version;
}