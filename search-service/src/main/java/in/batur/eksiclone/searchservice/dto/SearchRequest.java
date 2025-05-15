package in.batur.eksiclone.searchservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String query;
    private Set<String> types; // Optional: filter by types ("ENTRY", "TOPIC", "USER")
    private Set<String> tags; // Optional: filter by tags
    private Long authorId; // Optional: filter by author
    private LocalDateTime fromDate; // Optional: filter by creation date
    private LocalDateTime toDate; // Optional: filter by creation date
    private Boolean sortByDate; // Optional: sort by date (true) or relevance (false/null)
    private Boolean sortAscending; // Optional: sort direction
}
