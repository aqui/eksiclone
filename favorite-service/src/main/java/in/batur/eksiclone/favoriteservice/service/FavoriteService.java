package in.batur.eksiclone.favoriteservice.service;

import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FavoriteService {
    FavoriteDTO addFavorite(CreateFavoriteRequest request);
    
    void removeFavorite(Long userId, Long entryId);
    
    boolean isFavorite(Long userId, Long entryId);
    
    Page<FavoriteDTO> getUserFavorites(Long userId, Pageable pageable);
    
    Page<FavoriteDTO> getEntryFavorites(Long entryId, Pageable pageable);
    
    long countEntryFavorites(Long entryId);
    
    List<Long> getUserFavoriteEntryIds(Long userId);
}