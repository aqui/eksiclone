package in.batur.eksiclone.favoriteservice.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;

public interface FavoriteService {
    FavoriteDTO createFavorite(CreateFavoriteRequest request);
    
    void deleteFavorite(Long id);
    
    void deleteFavoriteByUserAndEntry(Long userId, Long entryId);
    
    boolean checkFavorite(Long userId, Long entryId);
    
    List<FavoriteDTO> getFavoritesByUser(Long userId);
    
    Page<FavoriteDTO> getFavoritesByUserPaged(Long userId, Pageable pageable);
    
    List<FavoriteDTO> getFavoritesByEntry(Long entryId);
    
    Page<FavoriteDTO> getFavoritesByEntryPaged(Long entryId, Pageable pageable);
    
    Long countFavoritesByEntry(Long entryId);
}
