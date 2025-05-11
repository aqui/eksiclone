package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.userservice.dto.RoleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final RoleMapper roleMapper;
    
    @Value("${rabbitmq.exchange.name:eksiclone-exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing.role-create:role.create}")
    private String roleCreateRoutingKey;
    
    @Value("${rabbitmq.routing.role-update:role.update}")
    private String roleUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.role-delete:role.delete}")
    private String roleDeleteRoutingKey;

    public RoleMessagingService(RabbitTemplate rabbitTemplate, RoleMapper roleMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.roleMapper = roleMapper;
    }

    public void sendRoleCreatedEvent(Role role) {
        try {
            RoleDTO roleDTO = roleMapper.toDto(role);
            log.info("Sending role created event for role: {}", role.getRoleName());
            rabbitTemplate.convertAndSend(exchangeName, roleCreateRoutingKey, roleDTO);
        } catch (Exception e) {
            log.error("Error sending role created event", e);
        }
    }

    public void sendRoleUpdatedEvent(Role role) {
        try {
            RoleDTO roleDTO = roleMapper.toDto(role);
            log.info("Sending role updated event for role: {}", role.getRoleName());
            rabbitTemplate.convertAndSend(exchangeName, roleUpdateRoutingKey, roleDTO);
        } catch (Exception e) {
            log.error("Error sending role updated event", e);
        }
    }

    public void sendRoleDeletedEvent(Role role) {
        try {
            RoleDTO roleDTO = roleMapper.toDto(role);
            log.info("Sending role deleted event for role: {}", role.getRoleName());
            rabbitTemplate.convertAndSend(exchangeName, roleDeleteRoutingKey, roleDTO);
        } catch (Exception e) {
            log.error("Error sending role deleted event", e);
        }
    }
}