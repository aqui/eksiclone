package in.batur.eksiclone.messageservice.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import in.batur.eksiclone.messageservice.dto.ApiResponse;
import in.batur.eksiclone.messageservice.dto.ConversationDTO;
import in.batur.eksiclone.messageservice.dto.MessageDTO;
import in.batur.eksiclone.messageservice.dto.SendMessageRequest;
import in.batur.eksiclone.messageservice.service.MessageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(@RequestBody @Valid SendMessageRequest request) {
        MessageDTO message = messageService.sendMessage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "Message sent successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDTO>> getMessage(@PathVariable Long id) {
        MessageDTO message = messageService.getMessage(id);
        return ResponseEntity.ok(new ApiResponse<>(message, "Message retrieved successfully"));
    }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        List<MessageDTO> conversation = messageService.getConversation(userId1, userId2);
        return ResponseEntity.ok(new ApiResponse<>(conversation, "Conversation retrieved successfully"));
    }

    @GetMapping("/conversation/paged")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getConversationPaged(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MessageDTO> conversation = messageService.getConversationPaged(userId1, userId2, pageable);
        return ResponseEntity.ok(new ApiResponse<>(conversation, "Conversation retrieved successfully"));
    }

    @GetMapping("/user/{userId}/conversations")
    public ResponseEntity<ApiResponse<List<ConversationDTO>>> getUserConversations(@PathVariable Long userId) {
        List<ConversationDTO> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(new ApiResponse<>(conversations, "User conversations retrieved successfully"));
    }

    @GetMapping("/user/{userId}/conversations/paged")
    public ResponseEntity<ApiResponse<Page<ConversationDTO>>> getUserConversationsPaged(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "lastMessage.createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ConversationDTO> conversations = messageService.getUserConversationsPaged(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(conversations, "User conversations retrieved successfully"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<MessageDTO>> markAsRead(@PathVariable Long id) {
        MessageDTO message = messageService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>(message, "Message marked as read"));
    }

    @PutMapping("/conversation/read")
    public ResponseEntity<ApiResponse<Void>> markConversationAsRead(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        messageService.markConversationAsRead(userId1, userId2);
        return ResponseEntity.ok(new ApiResponse<>(null, "Conversation marked as read"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Message deleted successfully"));
    }

    @DeleteMapping("/conversation")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        messageService.deleteConversation(userId1, userId2);
        return ResponseEntity.ok(new ApiResponse<>(null, "Conversation deleted successfully"));
    }

    @GetMapping("/user/{userId}/count-unread")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessages(@PathVariable Long userId) {
        Long count = messageService.countUnreadMessages(userId);
        return ResponseEntity.ok(new ApiResponse<>(count, "Unread message count retrieved successfully"));
    }
}
