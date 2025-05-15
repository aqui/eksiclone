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
public class CreateTopicRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;
    
    private String description;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    private Set<String> tags;
}