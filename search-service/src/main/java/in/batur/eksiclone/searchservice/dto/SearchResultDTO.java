package in.batur.eksiclone.searchservice.dto;

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
public class SearchResultDTO {
    private String type; // "ENTRY", "TOPIC", "USER"
    private Long id;
    private String title; // Topic title or username
    private String content; // Entry content or user bio
    private Long authorId; // For entries
    private String authorUsername; // For entries
    private Set<String> tags; // For entries and topics
    private int entryCount; // For topics
    private int favoriteCount; // For entries
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private Double relevanceScore;
    private String highlight; // Highlighted text snippet
}
