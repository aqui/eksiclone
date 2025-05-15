package in.batur.eksiclone.entryservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    private Long id;
    private String title;
    private String description;
    private int entryCount;
    private int viewCount;
    private Set<String> tags;
    private List<EntryPreviewDTO> recentEntries; // Son entry'ler eklendi
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}