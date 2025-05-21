package in.batur.eksiclone.fileservice.service;

import in.batur.eksiclone.fileservice.dto.FileDTO;
import in.batur.eksiclone.fileservice.dto.FileStatsDTO;
import in.batur.eksiclone.fileservice.dto.FileUploadResponse;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileUploadResponse storeFile(MultipartFile file, Long userId, boolean isPublic);
    
    Resource loadFileAsResource(String filename);
    
    FileDTO getFile(String filename);
    
    FileDTO getFileById(Long id);
    
    void deleteFile(Long id, Long userId);
    
    FileDTO updateFileVisibility(Long id, Long userId, boolean isPublic);
    
    Page<FileDTO> getUserFiles(Long userId, Pageable pageable);
    
    Page<FileDTO> getUserFilesByType(Long userId, String contentType, Pageable pageable);
    
    Page<FileDTO> getPublicFiles(Pageable pageable);
    
    Page<FileDTO> getPublicFilesByType(String contentType, Pageable pageable);
    
    FileDTO incrementDownloadCount(String filename);
    
    FileStatsDTO getUserFileStats(Long userId);
}
