package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.userservice.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final UserMapper userMapper;
    
    @Value("${rabbitmq.exchange.name:eksiclone-exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing.user-create:user.create}")
    private String userCreateRoutingKey;
    
    @Value("${rabbitmq.routing.user-update:user.update}")
    private String userUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.user-delete:user.delete}")
    private String userDeleteRoutingKey;

    public UserMessagingService(RabbitTemplate rabbitTemplate, UserMapper userMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.userMapper = userMapper;
    }

    public void sendUserCreatedEvent(User user) {
        try {
            UserDTO userDTO = userMapper.toDto(user);
            log.info("Sending user created event for user: {}", user.getUsername());
            rabbitTemplate.convertAndSend(exchangeName, userCreateRoutingKey, userDTO);
        } catch (Exception e) {
            log.error("Error sending user created event", e);
        }
    }

    public void sendUserUpdatedEvent(User user) {
        try {
            UserDTO userDTO = userMapper.toDto(user);
            log.info("Sending user updated event for user: {}", user.getUsername());
            rabbitTemplate.convertAndSend(exchangeName, userUpdateRoutingKey, userDTO);
        } catch (Exception e) {
            log.error("Error sending user updated event", e);
        }
    }

    public void sendUserDeletedEvent(User user) {
        try {
            UserDTO userDTO = userMapper.toDto(user);
            log.info("Sending user deleted event for user: {}", user.getUsername());
            rabbitTemplate.convertAndSend(exchangeName, userDeleteRoutingKey, userDTO);
        } catch (Exception e) {
            log.error("Error sending user deleted event", e);
        }
    }
}