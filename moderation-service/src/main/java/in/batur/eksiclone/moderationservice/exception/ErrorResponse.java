package in.batur.eksiclone.moderationservice.exception;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String error;
    
    public ErrorResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
    
    public ErrorResponse(String message, String error) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.error = error;
    }
}
