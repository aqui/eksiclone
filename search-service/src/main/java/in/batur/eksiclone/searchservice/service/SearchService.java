package in.batur.eksiclone.searchservice.service;
import org.springframework.data.domain.Pageable;
import in.batur.eksiclone.searchservice.dto.SearchRequest;
import in.batur.eksiclone.searchservice.dto.SearchResultDTO;
import in.batur.eksiclone.searchservice.dto.SearchResultsDTO;
import java.util.List;

public interface SearchService {
    SearchResultsDTO search(SearchRequest request, Pageable pageable);
    
    List<SearchResultDTO> quickSearch(String query, int limit);
    
    List<String> suggestTags(String prefix, int limit);
    
    List<String> suggestTopics(String prefix, int limit);
    
    List<String> suggestUsers(String prefix, int limit);
    
    List<String> getPopularSearches(int limit);
    
    void recordSearch(String query, String type);
}
