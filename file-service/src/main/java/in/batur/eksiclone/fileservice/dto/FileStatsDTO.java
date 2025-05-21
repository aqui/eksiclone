package in.batur.eksiclone.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStatsDTO {
    private Long userId;
    private String username;
    private Long totalFiles;
    private Long totalSize;
    private String readableSize; // like "1.5 MB"
}
