package com.saga.airlinesystem.userservice.rabbitmq;

import com.saga.airlinesystem.userservice.dto.ReservationMessageDto;
import com.saga.airlinesystem.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.userservice.rabbitmq.RabbitConfiguration.USER_QUEUE;

@Service
@RequiredArgsConstructor
public class RabbitListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = USER_QUEUE)
    public void handle(String payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        ReservationMessageDto reservationMessageDto = objectMapper.readValue(payload, ReservationMessageDto.class);
        switch (routingKey) {
            case "reservation.created":
                handleReservationCreatedEvent(reservationMessageDto);
                break;
            case "reservation.successful":
                handleReservationSuccesful(reservationMessageDto);
                break;
            default:
                System.out.println("Unknown routingKey: " + routingKey);
        }
    }

    private void handleReservationCreatedEvent(ReservationMessageDto reservationMessageDto) {
        userService.checkIfUserIsBlacklisted(reservationMessageDto);
    }

    private void handleReservationSuccesful(ReservationMessageDto reservationMessageDto) {
        System.out.println("Reservation successful");
    }
}
