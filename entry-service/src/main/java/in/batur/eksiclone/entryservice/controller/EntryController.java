package in.batur.eksiclone.entryservice.controller;

import in.batur.eksiclone.entryservice.dto.EntryDTO;
import in.batur.eksiclone.entryservice.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/entries")
@Tag(name = "Entry API", description = "APIs for managing entries in Eksiclone")
public class EntryController {
    
    private final EntryService entryService;
    
    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }
    
    @Operation(summary = "Create a new entry")
    @ApiResponse(responseCode = "201", description = "Entry created successfully")
    @PostMapping
    public ResponseEntity<EntryDTO> createEntry(@Valid @RequestBody EntryDTO entryDTO) {
        EntryDTO createdEntry = entryService.createEntry(entryDTO);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get an entry by ID")
    @ApiResponse(responseCode = "200", description = "Entry found")
    @GetMapping("/{id}")
    public ResponseEntity<EntryDTO> getEntryById(@PathVariable Long id) {
        EntryDTO entryDTO = entryService.getEntryById(id);
        return ResponseEntity.ok(entryDTO);
    }
    
    @Operation(summary = "Get entries by topic ID")
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<Page<EntryDTO>> getEntriesByTopicId(
            @PathVariable Long topicId,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {
        Page<EntryDTO> entries = entryService.getEntriesByTopicId(topicId, pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Get entries by topic ID ordered by favorites")
    @GetMapping("/by-topic/{topicId}/popular")
    public ResponseEntity<Page<EntryDTO>> getEntriesByTopicIdOrderByFavorites(
            @PathVariable Long topicId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EntryDTO> entries = entryService.getEntriesByTopicIdOrderByFavorites(topicId, pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Get entries by topic ID ordered by newest")
    @GetMapping("/by-topic/{topicId}/newest")
    public ResponseEntity<Page<EntryDTO>> getEntriesByTopicIdOrderByNewest(
            @PathVariable Long topicId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EntryDTO> entries = entryService.getEntriesByTopicIdOrderByNewest(topicId, pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Get entries by author ID")
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<Page<EntryDTO>> getEntriesByAuthorId(
            @PathVariable Long authorId,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {
        Page<EntryDTO> entries = entryService.getEntriesByAuthorId(authorId, pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Search entries by keyword")
    @GetMapping("/search")
    public ResponseEntity<Page<EntryDTO>> searchEntries(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EntryDTO> entries = entryService.searchEntries(keyword, pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Get popular entries")
    @GetMapping("/popular")
    public ResponseEntity<Page<EntryDTO>> getPopularEntries(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EntryDTO> entries = entryService.getPopularEntries(pageable);
        return ResponseEntity.ok(entries);
    }
    
    @Operation(summary = "Update an entry")
    @ApiResponse(responseCode = "200", description = "Entry updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<EntryDTO> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody EntryDTO entryDTO) {
        EntryDTO updatedEntry = entryService.updateEntry(id, entryDTO);
        return ResponseEntity.ok(updatedEntry);
    }
    
    @Operation(summary = "Delete an entry")
    @ApiResponse(responseCode = "204", description = "Entry deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Increment favorite count")
    @PutMapping("/{id}/favorite")
    public ResponseEntity<EntryDTO> incrementFavoriteCount(@PathVariable Long id) {
        EntryDTO entryDTO = entryService.incrementFavoriteCount(id);
        return ResponseEntity.ok(entryDTO);
    }
    
    @Operation(summary = "Decrement favorite count")
    @PutMapping("/{id}/unfavorite")
    public ResponseEntity<EntryDTO> decrementFavoriteCount(@PathVariable Long id) {
        EntryDTO entryDTO = entryService.decrementFavoriteCount(id);
        return ResponseEntity.ok(entryDTO);
    }
    
    @Operation(summary = "Count entries by topic ID")
    @GetMapping("/count/by-topic/{topicId}")
    public ResponseEntity<Long> countEntriesByTopicId(@PathVariable Long topicId) {
        Long count = entryService.countEntriesByTopicId(topicId);
        return ResponseEntity.ok(count);
    }
}