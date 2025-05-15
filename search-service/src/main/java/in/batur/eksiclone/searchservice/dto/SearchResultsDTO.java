package in.batur.eksiclone.searchservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultsDTO {
    private List<SearchResultDTO> results;
    private int totalResults;
    private int pageSize;
    private int currentPage;
    private int totalPages;
    private String searchQuery;
    private long searchTimeMs;
}
