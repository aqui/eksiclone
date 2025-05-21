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
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotNull(message = "Entry ID cannot be null")
    private Long entryId;
}