package com.saga.airlinesystem.passengerservice.rabbitmq;

import com.saga.airlinesystem.passengerservice.exceptions.customexceptions.EventAlreadyReceivedException;
import com.saga.airlinesystem.passengerservice.inboxevents.InboxEventService;
import com.saga.airlinesystem.passengerservice.inboxevents.InboxEventType;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitListener {

    private final ObjectMapper objectMapper;
    private final InboxEventService inboxEventService;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = PASSENGER_QUEUE)
    public void handle(String payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            switch (routingKey) {
                case PASSENGER_VALIDATION_REQUEST_KEY:
                    handlePassengerValidationRequest(payload);
                    break;
                case UPDATE_PASSENGER_MILES_REQUEST_KEY:
                    handleUpdatePassengerMilesRequest(payload);
                    break;
                default:
                    System.out.println("Unknown routingKey: " + routingKey);
            }
        } catch (EventAlreadyReceivedException e) {
            log.warn(e.getMessage());
        } catch (JacksonException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

    private void handlePassengerValidationRequest(String payload) {
        ValidatePassengerRequestMessage message = objectMapper.readValue(payload, ValidatePassengerRequestMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.PASSENGER_VALIDATION_REQUEST);
        log.info("Received passenger validation request for ticket order id: {}", message.getTicketOrderId());
    }

    private void handleUpdatePassengerMilesRequest(String payload) {
        UpdatePassengerMilesRequestMessage message = objectMapper.readValue(payload, UpdatePassengerMilesRequestMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.PASSENGER_UPDATE_MILES_REQUEST);
        log.info("Received update miles request for ticket order id: {}", message.getTicketOrderId());
    }
}
