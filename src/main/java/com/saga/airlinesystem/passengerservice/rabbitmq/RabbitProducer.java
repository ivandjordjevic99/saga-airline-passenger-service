package com.saga.airlinesystem.passengerservice.rabbitmq;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UserValidationResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserValidatedEvent(UserValidationResultMessage payloadObject) {
        String payload = objectMapper.writeValueAsString(payloadObject);
        sendEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATED_KEY, payload);
    }

    public void sendUserValidationFailedEvent(UserValidationResultMessage payloadObject) {
        String payload = objectMapper.writeValueAsString(payloadObject);
        sendEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_FAILED_KEY, payload);
    }

    public void sendEvent(String exchange, String routingKey, String payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
