package in.batur.eksiclone.entryservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private Long id;
    private String name;
    private String description;
    private int entryCount;
    private int topicCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}