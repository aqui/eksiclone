package in.batur.eksiclone.messageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long userId;
    private String username;
    private MessageDTO lastMessage;
    private int unreadCount;
}
