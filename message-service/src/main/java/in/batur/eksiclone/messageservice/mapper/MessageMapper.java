package in.batur.eksiclone.messageservice.mapper;

import in.batur.eksiclone.entity.message.Message;
import in.batur.eksiclone.messageservice.dto.MessageDTO;

import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDto(Message message) {
        if (message == null) {
            return null;
        }
        
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .receiverId(message.getReceiver().getId())
                .receiverUsername(message.getReceiver().getUsername())
                .content(message.getContent())
                .isRead(message.isRead())
                .createdDate(message.getCreatedDate())
                .build();
    }
}
