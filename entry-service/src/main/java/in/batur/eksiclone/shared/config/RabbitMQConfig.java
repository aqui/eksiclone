package in.batur.eksiclone.shared.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name:eksiclone-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.topic-events:topic-events-queue}")
    private String topicEventsQueue;
    
    @Value("${rabbitmq.queue.entry-events:entry-events-queue}")
    private String entryEventsQueue;

    @Value("${rabbitmq.routing.topic-create:topic.create}")
    private String topicCreateRoutingKey;
    
    @Value("${rabbitmq.routing.topic-update:topic.update}")
    private String topicUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.topic-delete:topic.delete}")
    private String topicDeleteRoutingKey;
    
    @Value("${rabbitmq.routing.entry-create:entry.create}")
    private String entryCreateRoutingKey;
    
    @Value("${rabbitmq.routing.entry-update:entry.update}")
    private String entryUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.entry-delete:entry.delete}")
    private String entryDeleteRoutingKey;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Queue topicEventsQueue() {
        return new Queue(topicEventsQueue, true);
    }
    
    @Bean
    Queue entryEventsQueue() {
        return new Queue(entryEventsQueue, true);
    }

    @Bean
    Binding topicCreateBinding(Queue topicEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(topicEventsQueue).to(exchange).with(topicCreateRoutingKey);
    }
    
    @Bean
    Binding topicUpdateBinding(Queue topicEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(topicEventsQueue).to(exchange).with(topicUpdateRoutingKey);
    }
    
    @Bean
    Binding topicDeleteBinding(Queue topicEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(topicEventsQueue).to(exchange).with(topicDeleteRoutingKey);
    }
    
    @Bean
    Binding entryCreateBinding(Queue entryEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(entryEventsQueue).to(exchange).with(entryCreateRoutingKey);
    }
    
    @Bean
    Binding entryUpdateBinding(Queue entryEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(entryEventsQueue).to(exchange).with(entryUpdateRoutingKey);
    }
    
    @Bean
    Binding entryDeleteBinding(Queue entryEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(entryEventsQueue).to(exchange).with(entryDeleteRoutingKey);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}