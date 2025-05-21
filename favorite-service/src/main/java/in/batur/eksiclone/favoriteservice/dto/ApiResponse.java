package in.batur.eksiclone.favoriteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean success;
    
    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = true;
    }
}