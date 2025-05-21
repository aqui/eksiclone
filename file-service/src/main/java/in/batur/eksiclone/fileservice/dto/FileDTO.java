package in.batur.eksiclone.fileservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private Long id;
    private String filename;
    private String originalFilename;
    private String contentType;
    private long size;
    private Long ownerId;
    private String ownerUsername;
    private boolean isPublic;
    private int downloadCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private String downloadUrl;
}
