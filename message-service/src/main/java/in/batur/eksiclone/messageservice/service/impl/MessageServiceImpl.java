package in.batur.eksiclone.messageservice.service.impl;

import in.batur.eksiclone.entity.message.Message;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.messageservice.dto.CreateMessageRequest;
import in.batur.eksiclone.messageservice.dto.MessageDTO;
import in.batur.eksiclone.messageservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.messageservice.mapper.MessageMapper;
import in.batur.eksiclone.messageservice.service.MessageService;
import in.batur.eksiclone.repository.message.MessageRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            UserRepository userRepository,
            MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"messages"}, allEntries = true)
    public MessageDTO sendMessage(CreateMessageRequest request) {
        User sender = findUserById(request.getSenderId());
        User receiver = findUserById(request.getReceiverId());
        
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        
        message = messageRepository.save(message);
        
        return messageMapper.toDto(message);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'message:' + #id")
    public MessageDTO getMessage(Long id) {
        Message message = findMessageById(id);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"messages"}, allEntries = true)
    public void deleteMessageForSender(Long messageId, Long senderId) {
        Message message = findMessageById(messageId);
        
        // Verify the sender
        if (!message.getSender().getId().equals(senderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this message");
        }
        
        message.setDeletedBySender(true);
        
        // If both sender and receiver deleted, actually delete the message
        if (message.isDeletedByReceiver()) {
            messageRepository.delete(message);
        } else {
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"messages"}, allEntries = true)
    public void deleteMessageForReceiver(Long messageId, Long receiverId) {
        Message message = findMessageById(messageId);
        
        // Verify the receiver
        if (!message.getReceiver().getId().equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this message");
        }
        
        message.setDeletedByReceiver(true);
        
        // If both sender and receiver deleted, actually delete the message
        if (message.isDeletedBySender()) {
            messageRepository.delete(message);
        } else {
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"messages"}, key = "'message:' + #messageId")
    public MessageDTO markAsRead(Long messageId) {
        Message message = findMessageById(messageId);
        message.setRead(true);
        message = messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'sent:' + #senderId + ':' + #pageable")
    public Page<MessageDTO> getSentMessages(Long senderId, Pageable pageable) {
        User sender = findUserById(senderId);
        return messageRepository.findBySenderAndIsDeletedBySenderFalseOrderByCreatedDateDesc(sender, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'received:' + #receiverId + ':' + #pageable")
    public Page<MessageDTO> getReceivedMessages(Long receiverId, Pageable pageable) {
        User receiver = findUserById(receiverId);
        return messageRepository.findByReceiverAndIsDeletedByReceiverFalseOrderByCreatedDateDesc(receiver, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'conversation:' + #userId1 + ':' + #userId2 + ':' + #pageable")
    public Page<MessageDTO> getConversation(Long userId1, Long userId2, Pageable pageable) {
        // This is a placeholder implementation - in a real app you'd need a proper query
        // We'll just return sent messages for now
        User sender = findUserById(userId1);
        return messageRepository.findBySenderAndIsDeletedBySenderFalseOrderByCreatedDateDesc(sender, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'unread:' + #receiverId")
    public long getUnreadMessageCount(Long receiverId) {
        User receiver = findUserById(receiverId);
        return messageRepository.countUnreadMessagesByReceiver(receiver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getLatestMessagesByUser(Long userId, int limit) {
        User user = findUserById(userId);
        
        // Get latest messages by created date
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Message> messages = messageRepository.findByUserOrderByCreatedDateDesc(user, pageable);
        
        return messages.getContent().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "'contacts:' + #userId")
    public List<Long> getMessageContacts(Long userId) {
        User user = findUserById(userId);
        
        // Get all users this user has communicated with
        return messageRepository.findMessageContactsByUser(user).stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
