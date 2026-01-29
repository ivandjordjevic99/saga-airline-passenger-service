package com.saga.airlinesystem.userservice.outboxevents;

import com.saga.airlinesystem.userservice.rabbitmq.messages.BaseMessage;

public interface OutboxEventService {

    void persistOutboxEvent(String exchange, String routingKey, BaseMessage payload);
}
