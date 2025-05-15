package in.batur.eksiclone.favoriteservice.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoriteRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Entry ID is required")
    private Long entryId;
}
