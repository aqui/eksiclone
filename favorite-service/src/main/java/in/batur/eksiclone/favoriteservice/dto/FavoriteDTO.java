package in.batur.eksiclone.favoriteservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long entryId;
    private String entryPreview;
    private LocalDateTime createdDate;
}
