package in.batur.eksiclone.favoriteservice.controller;

import in.batur.eksiclone.favoriteservice.dto.ApiResponse;
import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;
import in.batur.eksiclone.favoriteservice.service.FavoriteService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteDTO>> addFavorite(@RequestBody @Validated CreateFavoriteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(favoriteService.addFavorite(request), "Entry added to favorites"));
    }

    @DeleteMapping("/{userId}/{entryId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long entryId) {
        favoriteService.removeFavorite(userId, entryId);
        return ResponseEntity.ok(new ApiResponse<>(null, "Entry removed from favorites"));
    }

    @GetMapping("/check/{userId}/{entryId}")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @PathVariable Long userId,
            @PathVariable Long entryId) {
        return ResponseEntity.ok(new ApiResponse<>(
                favoriteService.isFavorite(userId, entryId),
                "Favorite status retrieved"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getUserFavorites(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                favoriteService.getUserFavorites(userId, pageable),
                "User favorites retrieved"));
    }

    @GetMapping("/entry/{entryId}")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getEntryFavorites(
            @PathVariable Long entryId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                favoriteService.getEntryFavorites(entryId, pageable),
                "Entry favorites retrieved"));
    }

    @GetMapping("/count/{entryId}")
    public ResponseEntity<ApiResponse<Long>> countEntryFavorites(@PathVariable Long entryId) {
        return ResponseEntity.ok(new ApiResponse<>(
                favoriteService.countEntryFavorites(entryId),
                "Favorite count retrieved"));
    }

    @GetMapping("/user/{userId}/entry-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getUserFavoriteEntryIds(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(
                favoriteService.getUserFavoriteEntryIds(userId),
                "Favorite entry IDs retrieved"));
    }
}