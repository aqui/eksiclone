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
public class EntryPreviewDTO {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private int favoriteCount;
    private LocalDateTime createdDate;
}