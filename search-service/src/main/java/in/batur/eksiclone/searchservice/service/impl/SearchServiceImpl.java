package in.batur.eksiclone.searchservice.service.impl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import in.batur.eksiclone.searchservice.dto.SearchRequest;
import in.batur.eksiclone.searchservice.dto.SearchResultDTO;
import in.batur.eksiclone.searchservice.dto.SearchResultsDTO;
import in.batur.eksiclone.searchservice.service.SearchService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class SearchServiceImpl implements SearchService {

    @Override
    public SearchResultsDTO search(SearchRequest request, Pageable pageable) {
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query is required");
        }
        
        // Start timer for search performance tracking
        long startTime = System.currentTimeMillis();
        
        // Mock search results
        List<SearchResultDTO> results = generateMockSearchResults(request, 50);
        
        // Apply pagination
        int totalResults = results.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalResults);
        List<SearchResultDTO> pagedResults = results.subList(start, end);
        
        // Calculate search time
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Build response
        return SearchResultsDTO.builder()
                .results(pagedResults)
                .totalResults(totalResults)
                .pageSize(pageable.getPageSize())
                .currentPage(pageable.getPageNumber())
                .totalPages((int) Math.ceil((double) totalResults / pageable.getPageSize()))
                .searchQuery(request.getQuery())
                .searchTimeMs(searchTime)
                .build();
    }

    @Override
    public List<SearchResultDTO> quickSearch(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query is required");
        }
        
        if (limit <= 0) {
            limit = 5; // Default limit
        }
        
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .build();
        
        // Generate mock results and limit them
        List<SearchResultDTO> results = generateMockSearchResults(request, 20);
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<String> suggestTags(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag prefix is required");
        }
        
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock tag suggestions
        List<String> allTags = Arrays.asList(
                "programming", "politics", "science", "technology", "philosophy", 
                "psychology", "religion", "history", "art", "music", 
                "literature", "sports", "food", "travel", "health", 
                "education", "environment", "economy", "social", "culture"
        );
        
        // Filter tags that start with the prefix (case insensitive)
        return allTags.stream()
                .filter(tag -> tag.toLowerCase().startsWith(prefix.toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> suggestTopics(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic prefix is required");
        }
        
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock topic suggestions
        List<String> allTopics = Arrays.asList(
                "The Future of AI", "Climate Change Solutions", "Modern Philosophy", 
                "Digital Privacy", "Quantum Computing", "Space Exploration", 
                "Renewable Energy", "Cryptocurrency Trends", "Remote Work Culture", 
                "Sustainable Agriculture", "Mental Health Awareness", "Global Politics", 
                "Movie Reviews", "Book Recommendations", "Music Festivals"
        );
        
        // Filter topics that contain the prefix (case insensitive)
        return allTopics.stream()
                .filter(topic -> topic.toLowerCase().contains(prefix.toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> suggestUsers(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username prefix is required");
        }
        
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock user suggestions
        List<String> allUsers = Arrays.asList(
                "johndoe", "janedoe", "bobsmith", "alicewilliams", "sarahjones", 
                "mikebrown", "emmadavis", "davidmiller", "oliviataylor", "jameswilson", 
                "sophiamoore", "williamjohnson", "avaanderson", "noahmartinez", "isabellathomas"
        );
        
        // Filter users that start with the prefix (case insensitive)
        return allUsers.stream()
                .filter(user -> user.toLowerCase().startsWith(prefix.toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPopularSearches(int limit) {
        if (limit <= 0) {
            limit = 10; // Default limit
        }
        
        // Mock popular searches
        List<String> popularSearches = Arrays.asList(
                "latest news", "politics", "artificial intelligence", "climate change", 
                "new movies", "covid updates", "stock market", "cryptocurrency", 
                "sports results", "tech news", "health tips", "recipes", 
                "job market", "travel destinations", "gaming"
        );
        
        return popularSearches.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public void recordSearch(String query, String type) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query is required");
        }
        
        // In a real implementation, this would store the search in a database for analytics
    }
    
    // Helper method to generate mock search results
    private List<SearchResultDTO> generateMockSearchResults(SearchRequest request, int count) {
        List<SearchResultDTO> results = new ArrayList<>();
        String[] types = {"ENTRY", "TOPIC", "USER"};
        String[] tags = {"programming", "politics", "science", "technology", "philosophy"};
        @SuppressWarnings("unused")
		String query = request.getQuery().toLowerCase(); // Used for search term highlighting // Used for search term highlight
        
        for (int i = 1; i <= count; i++) {
            // Alternate between result types
            String type = types[i % types.length];
            
            // Create a sample tag set
            Set<String> tagSet = new HashSet<>();
            tagSet.add(tags[i % tags.length]);
            tagSet.add(tags[(i + 2) % tags.length]);
            
            // Create highlight with search term bolded
            String content = "This is sample content number " + i + " containing the search term \"" + 
                    request.getQuery() + "\" and some other text for context.";
            String highlight = content.replaceAll(
                    "(?i)(" + request.getQuery() + ")", 
                    "<strong></strong>"
            );
            
            // Apply filters if present
            if (request.getTypes() != null && !request.getTypes().isEmpty() && !request.getTypes().contains(type)) {
                continue; // Skip this result if type filter doesn't match
            }
            
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                boolean hasMatchingTag = false;
                for (String tag : request.getTags()) {
                    if (tagSet.contains(tag)) {
                        hasMatchingTag = true;
                        break;
                    }
                }
                if (!hasMatchingTag) {
                    continue; // Skip if no matching tag
                }
            }
            
            if (request.getAuthorId() != null && type.equals("ENTRY") && i % 5 != 0) {
                continue; // Skip if author filter doesn't match (arbitrary condition for demo)
            }
            
            LocalDateTime createdDate = LocalDateTime.now().minusDays(i);
            if (request.getFromDate() != null && createdDate.isBefore(request.getFromDate())) {
                continue; // Skip if before fromDate
            }
            
            if (request.getToDate() != null && createdDate.isAfter(request.getToDate())) {
                continue; // Skip if after toDate
            }
            
            // Build result
            SearchResultDTO result = SearchResultDTO.builder()
                    .type(type)
                    .id((long) i)
                    .title(type.equals("ENTRY") ? null : "Sample " + type.toLowerCase() + " " + i)
                    .content(content)
                    .authorId(type.equals("ENTRY") ? (long) (100 + i) : null)
                    .authorUsername(type.equals("ENTRY") ? "user" + (100 + i) : null)
                    .tags(type.equals("USER") ? null : tagSet)
                    .entryCount(type.equals("TOPIC") ? i * 10 : 0)
                    .favoriteCount(type.equals("ENTRY") ? i * 5 : 0)
                    .createdDate(createdDate)
                    .lastUpdatedDate(LocalDateTime.now().minusHours(i))
                    .relevanceScore(1.0 - (i * 0.01)) // Decreasing relevance score
                    .highlight(highlight)
                    .build();
            
            results.add(result);
        }
        
        // Sort results by date or relevance
        if (request.getSortByDate() != null && request.getSortByDate()) {
            boolean ascending = request.getSortAscending() != null && request.getSortAscending();
            if (ascending) {
                results.sort((r1, r2) -> r1.getCreatedDate().compareTo(r2.getCreatedDate()));
            } else {
                results.sort((r1, r2) -> r2.getCreatedDate().compareTo(r1.getCreatedDate()));
            }
        } else {
            // Default: sort by relevance (descending)
            results.sort((r1, r2) -> Double.compare(r2.getRelevanceScore(), r1.getRelevanceScore()));
        }
        
        return results;
    }
}
