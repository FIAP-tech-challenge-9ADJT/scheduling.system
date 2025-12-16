package tech.challenge.scheduling.system.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            CachingConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(4);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public DirectExchange notificationsExchange(
            @Value("${notifications.exchange:consultation.notifications.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public DirectExchange notificationsDelayExchange(
            @Value("${notifications.delay.exchange:consultation.notifications.delay.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue consultationNotificationsQueue(
            @Value("${notifications.queue:consultation.notifications}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding notificationsBinding(
            Queue consultationNotificationsQueue,
            DirectExchange notificationsExchange,
            @Value("${notifications.routing-key:notification.consultation}") String routingKey) {
        return BindingBuilder.bind(consultationNotificationsQueue)
                .to(notificationsExchange)
                .with(routingKey);
    }

    @Bean
    public Queue consultationNotificationsDelayQueue(
            @Value("${notifications.delay.queue:consultation.notifications.delay}") String queueName,
            @Value("${notifications.exchange:consultation.notifications.exchange}") String dlx,
            @Value("${notifications.routing-key:notification.consultation}") String dlrk,
            @Value("${notifications.delay.ttl-ms:300000}") long ttlMs) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", dlx);
        args.put("x-dead-letter-routing-key", dlrk);
        args.put("x-message-ttl", ttlMs);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Binding notificationsDelayBinding(
            Queue consultationNotificationsDelayQueue,
            DirectExchange notificationsDelayExchange,
            @Value("${notifications.delay.routing-key:notification.consultation.delay}") String delayRoutingKey) {
        return BindingBuilder.bind(consultationNotificationsDelayQueue)
                .to(notificationsDelayExchange)
                .with(delayRoutingKey);
    }
}
