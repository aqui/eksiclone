package in.batur.eksiclone.fileservice.mapper;

import in.batur.eksiclone.entity.file.FileEntity;
import in.batur.eksiclone.fileservice.dto.FileDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {
    
    @Value("${file.download-url-prefix}")
    private String downloadUrlPrefix;

    public FileDTO toDto(FileEntity file) {
        if (file == null) {
            return null;
        }
        
        return FileDTO.builder()
                .id(file.getId())
                .filename(file.getFilename())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .ownerId(file.getOwner().getId())
                .ownerUsername(file.getOwner().getUsername())
                .isPublic(file.isPublic())
                .downloadCount(file.getDownloadCount())
                .createdDate(file.getCreatedDate())
                .lastUpdatedDate(file.getLastUpdatedDate())
                .downloadUrl(downloadUrlPrefix + "/" + file.getFilename())
                .build();
    }
}
