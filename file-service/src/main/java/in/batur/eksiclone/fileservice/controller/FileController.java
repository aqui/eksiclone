package in.batur.eksiclone.fileservice.controller;

import in.batur.eksiclone.fileservice.dto.ApiResponse;
import in.batur.eksiclone.fileservice.dto.FileDTO;
import in.batur.eksiclone.fileservice.dto.FileStatsDTO;
import in.batur.eksiclone.fileservice.dto.FileUploadResponse;
import in.batur.eksiclone.fileservice.service.FileStorageService;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean isPublic) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(fileStorageService.storeFile(file, userId, isPublic), "File uploaded successfully"));
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        // Increment download count
        fileStorageService.incrementDownloadCount(filename);
        
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default content type will be used
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Get file information to set proper headers
        FileDTO fileDTO = fileStorageService.getFile(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDTO.getOriginalFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<ApiResponse<FileDTO>> getFile(@PathVariable String filename) {
        return ResponseEntity.ok(new ApiResponse<>(fileStorageService.getFile(filename), "File information retrieved"));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<FileDTO>> getFileById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(fileStorageService.getFileById(id), "File information retrieved"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable Long id,
            @RequestParam Long userId) {
        fileStorageService.deleteFile(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(null, "File deleted successfully"));
    }

    @PutMapping("/visibility/{id}")
    public ResponseEntity<ApiResponse<FileDTO>> updateFileVisibility(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam boolean isPublic) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.updateFileVisibility(id, userId, isPublic),
                "File visibility updated successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<FileDTO>>> getUserFiles(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.getUserFiles(userId, pageable),
                "User files retrieved"));
    }

    @GetMapping("/user/{userId}/type/{contentType}")
    public ResponseEntity<ApiResponse<Page<FileDTO>>> getUserFilesByType(
            @PathVariable Long userId,
            @PathVariable String contentType,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.getUserFilesByType(userId, contentType, pageable),
                "User files by type retrieved"));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<FileDTO>>> getPublicFiles(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.getPublicFiles(pageable),
                "Public files retrieved"));
    }

    @GetMapping("/public/type/{contentType}")
    public ResponseEntity<ApiResponse<Page<FileDTO>>> getPublicFilesByType(
            @PathVariable String contentType,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.getPublicFilesByType(contentType, pageable),
                "Public files by type retrieved"));
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<ApiResponse<FileStatsDTO>> getUserFileStats(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(
                fileStorageService.getUserFileStats(userId),
                "User file statistics retrieved"));
    }
}
