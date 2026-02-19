package com.saga.airlinesystem.passengerservice.outboxevents;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.BaseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOutboxEvent(String exchange, String routingKey, BaseMessage payload) {
        OutboxEvent outboxEvent = new OutboxEvent(exchange, routingKey, objectMapper.writeValueAsString(payload));
        outboxEventRepository.save(outboxEvent);
    }
}
