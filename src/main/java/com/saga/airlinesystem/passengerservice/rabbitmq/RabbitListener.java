package com.saga.airlinesystem.passengerservice.rabbitmq;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import com.saga.airlinesystem.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
public class RabbitListener {

    private final PassengerService passengerService;
    private final ObjectMapper objectMapper;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = PASSENGER_QUEUE)
    public void handle(String payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        switch (routingKey) {
            case PASSENGER_VALIDATION_REQUEST_KEY:
                ValidatePassengerRequestMessage validatePassengerRequestMessage = objectMapper.readValue(payload, ValidatePassengerRequestMessage.class);
                handlePassengerValidationRequest(validatePassengerRequestMessage);
                break;
            case UPDATE_PASSENGER_MILES_REQUEST_KEY:
                UpdatePassengerMilesRequestMessage updatePassengerMilesRequestMessage = objectMapper.readValue(payload, UpdatePassengerMilesRequestMessage.class);
                handleUpdatePassengerMilesRequest(updatePassengerMilesRequestMessage);
                break;
            default:
                System.out.println("Unknown routingKey: " + routingKey);
        }
    }

    private void handlePassengerValidationRequest(ValidatePassengerRequestMessage message) {
        passengerService.checkIfPassengerIsBlacklisted(message);
    }

    private void handleUpdatePassengerMilesRequest(UpdatePassengerMilesRequestMessage message) {
        passengerService.updatePassengerMiles(message);
    }
}
