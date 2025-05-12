package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entity.Entry;
import in.batur.eksiclone.entryservice.dto.EntryDTO;
import in.batur.eksiclone.entryservice.mapper.EntryMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EntryMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final EntryMapper entryMapper;
    
    @Value("${rabbitmq.exchange.name:eksiclone-exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing.entry-create:entry.create}")
    private String entryCreateRoutingKey;
    
    @Value("${rabbitmq.routing.entry-update:entry.update}")
    private String entryUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.entry-delete:entry.delete}")
    private String entryDeleteRoutingKey;

    public EntryMessagingService(RabbitTemplate rabbitTemplate, EntryMapper entryMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.entryMapper = entryMapper;
    }

    public void sendEntryCreatedEvent(Entry entry) {
        EntryDTO entryDTO = entryMapper.toDto(entry);
        rabbitTemplate.convertAndSend(exchangeName, entryCreateRoutingKey, entryDTO);
    }

    public void sendEntryUpdatedEvent(Entry entry) {
        EntryDTO entryDTO = entryMapper.toDto(entry);
        rabbitTemplate.convertAndSend(exchangeName, entryUpdateRoutingKey, entryDTO);
    }

    public void sendEntryDeletedEvent(Entry entry) {
        EntryDTO entryDTO = entryMapper.toDto(entry);
        rabbitTemplate.convertAndSend(exchangeName, entryDeleteRoutingKey, entryDTO);
    }
}