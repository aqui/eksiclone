package in.batur.eksiclone.messageservice.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import in.batur.eksiclone.messageservice.dto.ConversationDTO;
import in.batur.eksiclone.messageservice.dto.MessageDTO;
import in.batur.eksiclone.messageservice.dto.SendMessageRequest;
import in.batur.eksiclone.messageservice.service.MessageService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public MessageDTO sendMessage(SendMessageRequest request) {
        // Mock implementation
        if (request.getSenderId() == null || request.getReceiverId() == null || request.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sender ID, Receiver ID, and content are required");
        }
        
        // Mock message creation
        return MessageDTO.builder()
                .id(1L)
                .senderId(request.getSenderId())
                .senderUsername("user" + request.getSenderId())
                .receiverId(request.getReceiverId())
                .receiverUsername("user" + request.getReceiverId())
                .content(request.getContent())
                .isRead(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Override
    public MessageDTO getMessage(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message ID");
        }
        
        // Mock message retrieval
        return MessageDTO.builder()
                .id(id)
                .senderId(101L)
                .senderUsername("user101")
                .receiverId(202L)
                .receiverUsername("user202")
                .content("This is a sample message with ID " + id)
                .isRead(false)
                .createdDate(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Override
    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        validateUserIds(userId1, userId2);
        
        // Generate mock conversation
        List<MessageDTO> conversation = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            boolean fromUser1 = i % 2 == 0;
            Long senderId = fromUser1 ? userId1 : userId2;
            Long receiverId = fromUser1 ? userId2 : userId1;
            
            conversation.add(MessageDTO.builder()
                    .id((long) i)
                    .senderId(senderId)
                    .senderUsername("user" + senderId)
                    .receiverId(receiverId)
                    .receiverUsername("user" + receiverId)
                    .content("Message " + i + " in conversation")
                    .isRead(i <= 5) // Older messages are read
                    .createdDate(LocalDateTime.now().minusMinutes(i * 10))
                    .build());
        }
        
        return conversation;
    }

    @Override
    public Page<MessageDTO> getConversationPaged(Long userId1, Long userId2, Pageable pageable) {
        List<MessageDTO> conversation = getConversation(userId1, userId2);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), conversation.size());
        
        return new PageImpl<>(
                conversation.subList(start, end),
                pageable,
                conversation.size());
    }

    @Override
    public List<ConversationDTO> getUserConversations(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Generate mock conversations
        List<ConversationDTO> conversations = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Long otherUserId = userId + i * 100;
            
            MessageDTO lastMessage = MessageDTO.builder()
                    .id((long) i)
                    .senderId(i % 2 == 0 ? userId : otherUserId)
                    .senderUsername("user" + (i % 2 == 0 ? userId : otherUserId))
                    .receiverId(i % 2 == 0 ? otherUserId : userId)
                    .receiverUsername("user" + (i % 2 == 0 ? otherUserId : userId))
                    .content("Last message in conversation " + i)
                    .isRead(i % 2 != 0) // Every other conversation has unread messages
                    .createdDate(LocalDateTime.now().minusHours(i))
                    .build();
            
            conversations.add(ConversationDTO.builder()
                    .userId(otherUserId)
                    .username("user" + otherUserId)
                    .lastMessage(lastMessage)
                    .unreadCount(i % 2 == 0 ? i : 0) // Even conversations have unread messages
                    .build());
        }
        
        return conversations;
    }

    @Override
    public Page<ConversationDTO> getUserConversationsPaged(Long userId, Pageable pageable) {
        List<ConversationDTO> conversations = getUserConversations(userId);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), conversations.size());
        
        return new PageImpl<>(
                conversations.subList(start, end),
                pageable,
                conversations.size());
    }

    @Override
    public MessageDTO markAsRead(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message ID");
        }
        
        // Mock update (in a real implementation, we would find and update the message)
        MessageDTO message = getMessage(id);
        message.setRead(true);
        
        return message;
    }

    @Override
    public void markConversationAsRead(Long userId1, Long userId2) {
        validateUserIds(userId1, userId2);
        
        // In a real implementation, we would update all unread messages in the conversation
    }

    @Override
    public void deleteMessage(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message ID");
        }
        
        // In a real implementation, we would find and delete the message
    }

    @Override
    public void deleteConversation(Long userId1, Long userId2) {
        validateUserIds(userId1, userId2);
        
        // In a real implementation, we would delete all messages between the users
    }

    @Override
    public Long countUnreadMessages(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
        
        // Mock count (in a real implementation, this would query the database)
        return 7L;
    }
    
    // Helper method to validate user IDs
    private void validateUserIds(Long userId1, Long userId2) {
        if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user IDs");
        }
        
        if (userId1.equals(userId2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs must be different");
        }
    }
}
