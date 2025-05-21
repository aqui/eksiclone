package in.batur.eksiclone.messageservice.controller;

import in.batur.eksiclone.messageservice.dto.ApiResponse;
import in.batur.eksiclone.messageservice.dto.CreateMessageRequest;
import in.batur.eksiclone.messageservice.dto.MessageDTO;
import in.batur.eksiclone.messageservice.service.MessageService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(@RequestBody @Validated CreateMessageRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.sendMessage(request), "Message sent successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDTO>> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage(id), "Message retrieved successfully"));
    }

    @DeleteMapping("/sender/{messageId}/{senderId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessageForSender(
            @PathVariable Long messageId,
            @PathVariable Long senderId) {
        messageService.deleteMessageForSender(messageId, senderId);
        return ResponseEntity.ok(new ApiResponse<>(null, "Message deleted for sender"));
    }

    @DeleteMapping("/receiver/{messageId}/{receiverId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessageForReceiver(
            @PathVariable Long messageId,
            @PathVariable Long receiverId) {
        messageService.deleteMessageForReceiver(messageId, receiverId);
        return ResponseEntity.ok(new ApiResponse<>(null, "Message deleted for receiver"));
    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<ApiResponse<MessageDTO>> markAsRead(@PathVariable Long messageId) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.markAsRead(messageId),
                "Message marked as read"));
    }

    @GetMapping("/sent/{senderId}")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getSentMessages(
            @PathVariable Long senderId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getSentMessages(senderId, pageable),
                "Sent messages retrieved"));
    }

    @GetMapping("/received/{receiverId}")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getReceivedMessages(
            @PathVariable Long receiverId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getReceivedMessages(receiverId, pageable),
                "Received messages retrieved"));
    }

    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getConversation(userId1, userId2, pageable),
                "Conversation retrieved"));
    }

    @GetMapping("/unread/count/{receiverId}")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(@PathVariable Long receiverId) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getUnreadMessageCount(receiverId),
                "Unread message count retrieved"));
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getLatestMessagesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getLatestMessagesByUser(userId, limit),
                "Latest messages retrieved"));
    }

    @GetMapping("/contacts/{userId}")
    public ResponseEntity<ApiResponse<List<Long>>> getMessageContacts(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getMessageContacts(userId),
                "Message contacts retrieved"));
    }
}
