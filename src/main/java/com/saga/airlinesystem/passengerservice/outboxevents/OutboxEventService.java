package com.saga.airlinesystem.passengerservice.outboxevents;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.BaseMessage;

public interface OutboxEventService {

    void saveOutboxEvent(String exchange, String routingKey, BaseMessage payload);
}
