package in.batur.eksiclone.entryservice.controller;

import in.batur.eksiclone.entryservice.dto.ApiResponse;
import in.batur.eksiclone.entryservice.dto.CreateEntryRequest;
import in.batur.eksiclone.entryservice.dto.EntryDTO;
import in.batur.eksiclone.entryservice.service.EntryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EntryDTO>> createEntry(@RequestBody @Validated CreateEntryRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(entryService.createEntry(request), "Entry created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntryDTO>> getEntry(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(entryService.getEntry(id), "Entry retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EntryDTO>> updateEntry(
            @PathVariable Long id,
            @RequestParam(required = true) String content,
            @RequestParam(required = false) Set<String> tags) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.updateEntry(id, content, tags), 
                "Entry updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Entry deleted successfully"));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> getEntriesByTopic(
            @PathVariable Long topicId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.getEntriesByTopic(topicId, pageable),
                "Entries retrieved successfully"));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> getEntriesByAuthor(
            @PathVariable Long authorId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.getEntriesByAuthor(authorId, pageable),
                "Entries retrieved successfully"));
    }
    
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> getEntriesByTag(
            @PathVariable String tagName,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.getEntriesByTag(tagName, pageable),
                "Entries retrieved successfully"));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> getLatestEntries(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.getLatestEntries(pageable),
                "Latest entries retrieved successfully"));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> getPopularEntries(
            @PageableDefault(size = 20, sort = "favoriteCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.getPopularEntries(pageable),
                "Popular entries retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EntryDTO>>> searchEntries(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.searchEntries(query, pageable),
                "Search results retrieved successfully"));
    }
    
    @PostMapping("/{id}/favorite/increment")
    public ResponseEntity<ApiResponse<EntryDTO>> incrementFavoriteCount(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.incrementFavoriteCount(id),
                "Favorite count incremented successfully"));
    }
    
    @PostMapping("/{id}/favorite/decrement")
    public ResponseEntity<ApiResponse<EntryDTO>> decrementFavoriteCount(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(
                entryService.decrementFavoriteCount(id),
                "Favorite count decremented successfully"));
    }
}