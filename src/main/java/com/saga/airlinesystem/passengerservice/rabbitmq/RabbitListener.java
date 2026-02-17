package com.saga.airlinesystem.passengerservice.rabbitmq;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.passengerservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
public class RabbitListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = USER_QUEUE)
    public void handle(String payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        switch (routingKey) {
            case USER_VALIDATION_REQUEST_KEY:
                ValidateUserRequestMessage validateUserRequestMessage = objectMapper.readValue(payload, ValidateUserRequestMessage.class);
                handleUserValidationRequest(validateUserRequestMessage);
                break;
            case UPDATE_USER_MILES_REQUEST_KEY:
                UpdateUserMilesRequestMessage updateUserMilesRequestMessage = objectMapper.readValue(payload, UpdateUserMilesRequestMessage.class);
                handleUpdateUserMilesRequest(updateUserMilesRequestMessage);
                break;
            default:
                System.out.println("Unknown routingKey: " + routingKey);
        }
    }

    private void handleUserValidationRequest(ValidateUserRequestMessage message) {
        userService.checkIfUserIsBlacklisted(message);
    }

    private void handleUpdateUserMilesRequest(UpdateUserMilesRequestMessage message) {
        userService.updateUserMiles(message);
    }
}
