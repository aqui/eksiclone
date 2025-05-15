package in.batur.eksiclone.entryservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {
    private Long id;
    private String content;
    private Long topicId;
    private String topicTitle;
    private Long authorId;
    private String authorUsername;
    private int favoriteCount;
    private Set<String> tags;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}