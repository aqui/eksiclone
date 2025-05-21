package in.batur.eksiclone.messageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    @NotNull(message = "Sender ID cannot be null")
    private Long senderId;
    
    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;
    
    @NotBlank(message = "Content cannot be empty")
    private String content;
}
