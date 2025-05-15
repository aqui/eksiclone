package in.batur.eksiclone.favoriteservice.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;
import in.batur.eksiclone.favoriteservice.service.FavoriteService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Override
    public FavoriteDTO createFavorite(CreateFavoriteRequest request) {
        // Mock implementation
        if (request.getUserId() == null || request.getEntryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Entry ID are required");
        }
        
        // Mock favorite creation
        return FavoriteDTO.builder()
                .id(1L)
                .userId(request.getUserId())
                .username("user" + request.getUserId())
                .entryId(request.getEntryId())
                .entryPreview("Sample entry content...")
                .createdDate(java.time.LocalDateTime.now())
                .build();
    }

    @Override
    public void deleteFavorite(Long id) {
        // Mock implementation
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid favorite ID");
        }
        
        // In a real implementation, we would check if the favorite exists and then delete it
    }

    @Override
    public void deleteFavoriteByUserAndEntry(Long userId, Long entryId) {
        // Mock implementation
        if (userId == null || userId <= 0 || entryId == null || entryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID or entry ID");
        }
        
        // In a real implementation, we would find the favorite by userId and entryId and then delete it
    }

    @Override
    public boolean checkFavorite(Long userId, Long entryId) {
        // Mock implementation
        if (userId == null || userId <= 0 || entryId == null || entryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID or entry ID");
        }
        
        // Mock response - in a real implementation, we would check if the favorite exists
        return userId % 2 == 0; // Just a mock logic: even user IDs have favorited
    }

    @Override
    public List<FavoriteDTO> getFavoritesByUser(Long userId) {
        // Mock implementation
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Mock data
        List<FavoriteDTO> favorites = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            favorites.add(FavoriteDTO.builder()
                    .id((long) i)
                    .userId(userId)
                    .username("user" + userId)
                    .entryId((long) (i * 10))
                    .entryPreview("Sample entry content " + i)
                    .createdDate(java.time.LocalDateTime.now().minusDays(i))
                    .build());
        }
        
        return favorites;
    }

    @Override
    public Page<FavoriteDTO> getFavoritesByUserPaged(Long userId, Pageable pageable) {
        // Mock implementation
        List<FavoriteDTO> favorites = getFavoritesByUser(userId);
        
        // Manual pagination (in a real implementation, this would use the repository)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), favorites.size());
        
        return new PageImpl<>(
                favorites.subList(start, end),
                pageable,
                favorites.size());
    }

    @Override
    public List<FavoriteDTO> getFavoritesByEntry(Long entryId) {
        // Mock implementation
        if (entryId == null || entryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid entry ID");
        }
        
        // Mock data
        List<FavoriteDTO> favorites = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            favorites.add(FavoriteDTO.builder()
                    .id((long) i)
                    .userId((long) (i * 100))
                    .username("user" + (i * 100))
                    .entryId(entryId)
                    .entryPreview("Sample entry content for entry " + entryId)
                    .createdDate(java.time.LocalDateTime.now().minusHours(i))
                    .build());
        }
        
        return favorites;
    }

    @Override
    public Page<FavoriteDTO> getFavoritesByEntryPaged(Long entryId, Pageable pageable) {
        // Mock implementation
        List<FavoriteDTO> favorites = getFavoritesByEntry(entryId);
        
        // Manual pagination (in a real implementation, this would use the repository)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), favorites.size());
        
        return new PageImpl<>(
                favorites.subList(start, end),
                pageable,
                favorites.size());
    }

    @Override
    public Long countFavoritesByEntry(Long entryId) {
        // Mock implementation
        if (entryId == null || entryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid entry ID");
        }
        
        // Mock count (in a real implementation, this would query the database)
        return 42L; // Mock count
    }
}
