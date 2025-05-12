package in.batur.eksiclone.topicservice.service;

import in.batur.eksiclone.entity.Topic;
import in.batur.eksiclone.topicservice.dto.TopicDTO;
import in.batur.eksiclone.topicservice.mapper.TopicMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TopicMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final TopicMapper topicMapper;
    
    @Value("${rabbitmq.exchange.name:eksiclone-exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing.topic-create:topic.create}")
    private String topicCreateRoutingKey;
    
    @Value("${rabbitmq.routing.topic-update:topic.update}")
    private String topicUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.topic-delete:topic.delete}")
    private String topicDeleteRoutingKey;

    public TopicMessagingService(RabbitTemplate rabbitTemplate, TopicMapper topicMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicMapper = topicMapper;
    }

    public void sendTopicCreatedEvent(Topic topic) {
        TopicDTO topicDTO = topicMapper.toDto(topic);
        rabbitTemplate.convertAndSend(exchangeName, topicCreateRoutingKey, topicDTO);
    }

    public void sendTopicUpdatedEvent(Topic topic) {
        TopicDTO topicDTO = topicMapper.toDto(topic);
        rabbitTemplate.convertAndSend(exchangeName, topicUpdateRoutingKey, topicDTO);
    }

    public void sendTopicDeletedEvent(Topic topic) {
        TopicDTO topicDTO = topicMapper.toDto(topic);
        rabbitTemplate.convertAndSend(exchangeName, topicDeleteRoutingKey, topicDTO);
    }
}