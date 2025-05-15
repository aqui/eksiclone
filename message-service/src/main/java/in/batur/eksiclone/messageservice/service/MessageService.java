package in.batur.eksiclone.messageservice.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import in.batur.eksiclone.messageservice.dto.ConversationDTO;
import in.batur.eksiclone.messageservice.dto.MessageDTO;
import in.batur.eksiclone.messageservice.dto.SendMessageRequest;

public interface MessageService {
    MessageDTO sendMessage(SendMessageRequest request);
    
    MessageDTO getMessage(Long id);
    
    List<MessageDTO> getConversation(Long userId1, Long userId2);
    
    Page<MessageDTO> getConversationPaged(Long userId1, Long userId2, Pageable pageable);
    
    List<ConversationDTO> getUserConversations(Long userId);
    
    Page<ConversationDTO> getUserConversationsPaged(Long userId, Pageable pageable);
    
    MessageDTO markAsRead(Long id);
    
    void markConversationAsRead(Long userId1, Long userId2);
    
    void deleteMessage(Long id);
    
    void deleteConversation(Long userId1, Long userId2);
    
    Long countUnreadMessages(Long userId);
}
