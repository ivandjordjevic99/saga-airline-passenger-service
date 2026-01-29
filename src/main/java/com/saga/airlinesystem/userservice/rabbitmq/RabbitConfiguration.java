package com.saga.airlinesystem.userservice.rabbitmq;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.saga.airlinesystem.userservice.rabbitmq.RabbitMQConstants.*;


@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange ticketReservationExchange() {
        return new TopicExchange(TICKET_RESERVATION_EXCHANGE);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Binding userRequestsBinding(Queue userQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(ticketReservationExchange)
                .with(USER_REQUESTS_TOPIC);
    }

}
