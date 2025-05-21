package in.batur.eksiclone.fileservice.service.impl;

import in.batur.eksiclone.entity.file.FileEntity;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.fileservice.config.FileStorageConfig;
import in.batur.eksiclone.fileservice.dto.FileDTO;
import in.batur.eksiclone.fileservice.dto.FileStatsDTO;
import in.batur.eksiclone.fileservice.dto.FileUploadResponse;
import in.batur.eksiclone.fileservice.exception.FileStorageException;
import in.batur.eksiclone.fileservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.fileservice.mapper.FileMapper;
import in.batur.eksiclone.fileservice.service.FileStorageService;
import in.batur.eksiclone.fileservice.util.FileUtils;
import in.batur.eksiclone.repository.file.FileRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileMapper fileMapper;
    private final FileUtils fileUtils;
    
    @Value("${file.download-url-prefix}")
    private String downloadUrlPrefix;

    public FileStorageServiceImpl(
            FileStorageConfig fileStorageConfig,
            FileRepository fileRepository,
            UserRepository userRepository,
            FileMapper fileMapper,
            FileUtils fileUtils) {
        this.fileStorageLocation = fileStorageConfig.getUploadPath();
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileMapper = fileMapper;
        this.fileUtils = fileUtils;
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"files"}, allEntries = true)
    public FileUploadResponse storeFile(MultipartFile file, Long userId, boolean isPublic) {
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }
            
            // Find user
            User user = findUserById(userId);
            
            // Generate unique filename
            String uniqueFilename = fileUtils.generateUniqueFilename(originalFilename);
            
            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Create file entity
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(uniqueFilename);
            fileEntity.setOriginalFilename(originalFilename);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setPath(targetLocation.toString());
            fileEntity.setOwner(user);
            fileEntity.setPublic(isPublic);
            
            fileEntity = fileRepository.save(fileEntity);
            
            return FileUploadResponse.builder()
                    .filename(fileEntity.getFilename())
                    .originalFilename(fileEntity.getOriginalFilename())
                    .contentType(fileEntity.getContentType())
                    .size(fileEntity.getSize())
                    .downloadUrl(downloadUrlPrefix + "/" + fileEntity.getFilename())
                    .build();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private FileEntity findFileById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }
    
    private FileEntity findFileByFilename(String filename) {
        return fileRepository.findByFilenameAndIsDeletedFalse(filename)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with filename: " + filename));
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            FileEntity fileEntity = findFileByFilename(filename);
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getFilename()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + filename, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'file:' + #filename")
    public FileDTO getFile(String filename) {
        FileEntity file = findFileByFilename(filename);
        return fileMapper.toDto(file);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'file_id:' + #id")
    public FileDTO getFileById(Long id) {
        FileEntity file = findFileById(id);
        return fileMapper.toDto(file);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"files"}, allEntries = true)
    public void deleteFile(Long id, Long userId) {
        FileEntity file = findFileById(id);
        
        // Check if user owns the file
        if (!file.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this file");
        }
        
        // Soft delete
        file.setDeleted(true);
        fileRepository.save(file);
        
        // Note: we don't actually delete the physical file here
        // A cleanup job could be created to remove files marked as deleted
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"files"}, key = "'file_id:' + #id")
    public FileDTO updateFileVisibility(Long id, Long userId, boolean isPublic) {
        FileEntity file = findFileById(id);
        
        // Check if user owns the file
        if (!file.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this file");
        }
        
        file.setPublic(isPublic);
        file = fileRepository.save(file);
        
        return fileMapper.toDto(file);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'user_files:' + #userId + ':' + #pageable")
    public Page<FileDTO> getUserFiles(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return fileRepository.findByOwnerAndIsDeletedFalseOrderByCreatedDateDesc(user, pageable)
                .map(fileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'user_files_by_type:' + #userId + ':' + #contentType + ':' + #pageable")
    public Page<FileDTO> getUserFilesByType(Long userId, String contentType, Pageable pageable) {
        User user = findUserById(userId);
        return fileRepository.findByOwnerAndContentTypeOrderByCreatedDateDesc(user, contentType, pageable)
                .map(fileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'public_files:' + #pageable")
    public Page<FileDTO> getPublicFiles(Pageable pageable) {
        return fileRepository.findByIsPublicTrueAndIsDeletedFalseOrderByCreatedDateDesc(pageable)
                .map(fileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'public_files_by_type:' + #contentType + ':' + #pageable")
    public Page<FileDTO> getPublicFilesByType(String contentType, Pageable pageable) {
        return fileRepository.findByContentTypeContainingAndIsDeletedFalseOrderByCreatedDateDesc(contentType, pageable)
                .map(fileMapper::toDto);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"files"}, key = "'file:' + #filename")
    public FileDTO incrementDownloadCount(String filename) {
        FileEntity file = findFileByFilename(filename);
        file.incrementDownloadCount();
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "files", key = "'user_stats:' + #userId")
    public FileStatsDTO getUserFileStats(Long userId) {
        User user = findUserById(userId);
        
        Long totalFiles = fileRepository.countByOwner(user);
        Long totalSize = fileRepository.getTotalSizeByOwner(user.getId());
        
        if (totalSize == null) {
            totalSize = 0L;
        }
        
        return FileStatsDTO.builder()
                .userId(userId)
                .username(user.getUsername())
                .totalFiles(totalFiles)
                .totalSize(totalSize)
                .readableSize(fileUtils.getReadableFileSize(totalSize))
                .build();
    }
}
