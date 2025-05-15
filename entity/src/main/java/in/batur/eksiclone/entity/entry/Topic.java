package in.batur.eksiclone.entity.entry;

import in.batur.eksiclone.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "topics")
@Getter
@Setter
public class Topic extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("topic")
    private List<Entry> entries = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "topic_tags",
        joinColumns = @JoinColumn(name = "topic_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("topics")
    private Set<Tag> tags = new HashSet<>();
    
    @Column(name = "entry_count")
    private int entryCount = 0;
    
    @Column(name = "view_count")
    private int viewCount = 0;
    
    private boolean isDeleted = false;
    
    public void addEntry(Entry entry) {
        entries.add(entry);
        entry.setTopic(this);
        entryCount++;
    }
    
    public void removeEntry(Entry entry) {
        entries.remove(entry);
        entry.setTopic(null);
        if (entryCount > 0) {
            entryCount--;
        }
    }
    
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getTopics().add(this);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getTopics().remove(this);
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
}