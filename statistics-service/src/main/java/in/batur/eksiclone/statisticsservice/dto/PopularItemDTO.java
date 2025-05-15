package in.batur.eksiclone.statisticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularItemDTO {
    private String type; // "ENTRY", "TOPIC", "USER", "TAG"
    private Long id;
    private String name; // Topic title, entry preview, username, or tag name
    private Long score; // View count, favorite count, entry count, etc.
}
