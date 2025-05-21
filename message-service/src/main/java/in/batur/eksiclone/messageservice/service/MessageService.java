package in.batur.eksiclone.messageservice.service;

import in.batur.eksiclone.messageservice.dto.CreateMessageRequest;
import in.batur.eksiclone.messageservice.dto.MessageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(CreateMessageRequest request);
    
    MessageDTO getMessage(Long id);
    
    void deleteMessageForSender(Long messageId, Long senderId);
    
    void deleteMessageForReceiver(Long messageId, Long receiverId);
    
    MessageDTO markAsRead(Long messageId);
    
    Page<MessageDTO> getSentMessages(Long senderId, Pageable pageable);
    
    Page<MessageDTO> getReceivedMessages(Long receiverId, Pageable pageable);
    
    Page<MessageDTO> getConversation(Long userId1, Long userId2, Pageable pageable);
    
    long getUnreadMessageCount(Long receiverId);
    
    List<MessageDTO> getLatestMessagesByUser(Long userId, int limit);
    
    List<Long> getMessageContacts(Long userId);
}
