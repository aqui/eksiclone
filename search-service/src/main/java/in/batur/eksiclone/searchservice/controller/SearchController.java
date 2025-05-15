package in.batur.eksiclone.searchservice.controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import in.batur.eksiclone.searchservice.dto.ApiResponse;
import in.batur.eksiclone.searchservice.dto.SearchRequest;
import in.batur.eksiclone.searchservice.dto.SearchResultDTO;
import in.batur.eksiclone.searchservice.dto.SearchResultsDTO;
import in.batur.eksiclone.searchservice.service.SearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResultsDTO>> search(
            @RequestParam String query,
            @RequestParam(required = false) Set<String> types,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            @RequestParam(required = false) Boolean sortByDate,
            @RequestParam(required = false) Boolean sortAscending,
            @PageableDefault(size = 20) Pageable pageable) {
        
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .types(types)
                .tags(tags)
                .authorId(authorId)
                .fromDate(fromDate)
                .toDate(toDate)
                .sortByDate(sortByDate)
                .sortAscending(sortAscending)
                .build();
        
        // Record the search
        searchService.recordSearch(query, types != null && types.size() == 1 ? types.iterator().next() : "ALL");
        
        SearchResultsDTO results = searchService.search(request, pageable);
        return ResponseEntity.ok(new ApiResponse<>(results, "Search results retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SearchResultsDTO>> searchWithBody(
            @RequestBody SearchRequest request,
            @PageableDefault(size = 20) Pageable pageable) {
        
        // Record the search
        searchService.recordSearch(request.getQuery(), 
                request.getTypes() != null && request.getTypes().size() == 1 ? 
                        request.getTypes().iterator().next() : "ALL");
        
        SearchResultsDTO results = searchService.search(request, pageable);
        return ResponseEntity.ok(new ApiResponse<>(results, "Search results retrieved successfully"));
    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<List<SearchResultDTO>>> quickSearch(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "5") int limit) {
        
        List<SearchResultDTO> results = searchService.quickSearch(query, limit);
        return ResponseEntity.ok(new ApiResponse<>(results, "Quick search results retrieved successfully"));
    }

    @GetMapping("/suggest/tags")
    public ResponseEntity<ApiResponse<List<String>>> suggestTags(
            @RequestParam String prefix,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<String> suggestions = searchService.suggestTags(prefix, limit);
        return ResponseEntity.ok(new ApiResponse<>(suggestions, "Tag suggestions retrieved successfully"));
    }

    @GetMapping("/suggest/topics")
    public ResponseEntity<ApiResponse<List<String>>> suggestTopics(
            @RequestParam String prefix,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<String> suggestions = searchService.suggestTopics(prefix, limit);
        return ResponseEntity.ok(new ApiResponse<>(suggestions, "Topic suggestions retrieved successfully"));
    }

    @GetMapping("/suggest/users")
    public ResponseEntity<ApiResponse<List<String>>> suggestUsers(
            @RequestParam String prefix,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<String> suggestions = searchService.suggestUsers(prefix, limit);
        return ResponseEntity.ok(new ApiResponse<>(suggestions, "User suggestions retrieved successfully"));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<String>>> getPopularSearches(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<String> popularSearches = searchService.getPopularSearches(limit);
        return ResponseEntity.ok(new ApiResponse<>(popularSearches, "Popular searches retrieved successfully"));
    }
}
