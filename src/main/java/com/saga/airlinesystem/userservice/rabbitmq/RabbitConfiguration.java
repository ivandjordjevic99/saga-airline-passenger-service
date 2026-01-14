package com.saga.airlinesystem.userservice.rabbitmq;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

    public static final String USER_QUEUE = "user.events";
    public static final String TICKET_RESERVATION_EXCHANGE = "ticket-reservation.exchange";

    @Bean
    public TopicExchange ticketReservationExchange() {
        return new TopicExchange(TICKET_RESERVATION_EXCHANGE);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Binding reservationCreatedBinding(Queue userQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(ticketReservationExchange)
                .with("reservation.created");
    }

    @Bean
    public Binding reservationSuccessfulBinding(Queue userQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(ticketReservationExchange)
                .with("reservation.successful");
    }



}
