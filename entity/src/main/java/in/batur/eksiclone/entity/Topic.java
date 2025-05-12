package in.batur.eksiclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
public class Topic extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String title;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "is_trending")
    private boolean trending;
    
    @Column(name = "view_count")
    private Long viewCount = 0L;
    
    @Column(name = "entry_count")
    private Long entryCount = 0L;
    
    @ElementCollection
    @CollectionTable(name = "topic_tags", joinColumns = @JoinColumn(name = "topic_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    @Version
    private Long version;
}