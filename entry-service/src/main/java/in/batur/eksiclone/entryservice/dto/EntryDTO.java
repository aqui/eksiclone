package in.batur.eksiclone.entryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EntryDTO {
    private Long id;

    @NotBlank(message = "Content is mandatory")
    @Size(min = 3, max = 10000, message = "Content must be between 3 and 10000 characters")
    private String content;

    @NotNull(message = "Topic ID is mandatory")
    private Long topicId;
    
    private String topicTitle;

    @NotNull(message = "Author ID is mandatory")
    private Long authorId;
    
    private String authorUsername;
    
    private Long favoriteCount;
    
    private boolean edited;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime lastUpdatedDate;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    
    public Long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }
    
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
}