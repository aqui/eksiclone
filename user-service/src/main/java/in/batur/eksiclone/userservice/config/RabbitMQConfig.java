package in.batur.eksiclone.userservice.config;

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

    @Value("${rabbitmq.queue.user-events:user-events-queue}")
    private String userEventsQueue;

    @Value("${rabbitmq.routing.user-create:user.create}")
    private String userCreateRoutingKey;
    
    @Value("${rabbitmq.routing.user-update:user.update}")
    private String userUpdateRoutingKey;
    
    @Value("${rabbitmq.routing.user-delete:user.delete}")
    private String userDeleteRoutingKey;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Queue userEventsQueue() {
        return new Queue(userEventsQueue, true);
    }

    @Bean
    Binding userCreateBinding(Queue userEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userEventsQueue).to(exchange).with(userCreateRoutingKey);
    }
    
    @Bean
    Binding userUpdateBinding(Queue userEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userEventsQueue).to(exchange).with(userUpdateRoutingKey);
    }
    
    @Bean
    Binding userDeleteBinding(Queue userEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userEventsQueue).to(exchange).with(userDeleteRoutingKey);
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