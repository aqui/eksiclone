package in.batur.eksiclone.entryservice.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntryRequest {
    @NotBlank(message = "Content cannot be empty")
    private String content;
    
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    @NotBlank(message = "Author username is required")
    private String authorUsername;
    
    private Set<String> tags;
}