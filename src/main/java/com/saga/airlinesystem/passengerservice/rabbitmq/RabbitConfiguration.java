package com.saga.airlinesystem.passengerservice.rabbitmq;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;


@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange ticketBookingExchange() {
        return new TopicExchange(TICKET_BOOKING_EXCHANGE);
    }

    @Bean
    public Queue passengerQueue() {
        return new Queue(PASSENGER_QUEUE, true);
    }

    @Bean
    public Binding passengerRequestsBinding(Queue passengerQueue, TopicExchange ticketBookingExchange) {
        return BindingBuilder
                .bind(passengerQueue)
                .to(ticketBookingExchange)
                .with(PASSENGER_REQUESTS_TOPIC);
    }

}
