package in.batur.eksiclone.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String filename;
    private String originalFilename;
    private String contentType;
    private long size;
    private String downloadUrl;
}
