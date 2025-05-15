package in.batur.eksiclone.favoriteservice.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import in.batur.eksiclone.favoriteservice.dto.ApiResponse;
import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;
import in.batur.eksiclone.favoriteservice.service.FavoriteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteDTO>> createFavorite(@RequestBody @Valid CreateFavoriteRequest request) {
        FavoriteDTO favorite = favoriteService.createFavorite(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(favorite, "Favorite created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Favorite deleted successfully"));
    }

    @DeleteMapping("/user/{userId}/entry/{entryId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavoriteByUserAndEntry(
            @PathVariable Long userId,
            @PathVariable Long entryId) {
        favoriteService.deleteFavoriteByUserAndEntry(userId, entryId);
        return ResponseEntity.ok(new ApiResponse<>(null, "Favorite deleted successfully"));
    }

    @GetMapping("/check/user/{userId}/entry/{entryId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFavorite(
            @PathVariable Long userId,
            @PathVariable Long entryId) {
        boolean isFavorited = favoriteService.checkFavorite(userId, entryId);
        return ResponseEntity.ok(new ApiResponse<>(isFavorited, "Favorite status retrieved"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FavoriteDTO>>> getFavoritesByUser(@PathVariable Long userId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(favorites, "Favorites retrieved successfully"));
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getFavoritesByUserPaged(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FavoriteDTO> favoritesPage = favoriteService.getFavoritesByUserPaged(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(favoritesPage, "Favorites retrieved successfully"));
    }

    @GetMapping("/entry/{entryId}")
    public ResponseEntity<ApiResponse<List<FavoriteDTO>>> getFavoritesByEntry(@PathVariable Long entryId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByEntry(entryId);
        return ResponseEntity.ok(new ApiResponse<>(favorites, "Favorites retrieved successfully"));
    }

    @GetMapping("/entry/{entryId}/paged")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getFavoritesByEntryPaged(
            @PathVariable Long entryId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FavoriteDTO> favoritesPage = favoriteService.getFavoritesByEntryPaged(entryId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(favoritesPage, "Favorites retrieved successfully"));
    }

    @GetMapping("/count/entry/{entryId}")
    public ResponseEntity<ApiResponse<Long>> countFavoritesByEntry(@PathVariable Long entryId) {
        Long count = favoriteService.countFavoritesByEntry(entryId);
        return ResponseEntity.ok(new ApiResponse<>(count, "Favorite count retrieved successfully"));
    }
}
