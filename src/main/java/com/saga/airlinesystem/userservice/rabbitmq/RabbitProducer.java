package com.saga.airlinesystem.userservice.rabbitmq;

import com.saga.airlinesystem.userservice.dto.ReservationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.userservice.rabbitmq.RabbitConfiguration.TICKET_RESERVATION_EXCHANGE;

@Service
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserOk(ReservationMessageDto reservationMessageDto) {
        send("user.validated", reservationMessageDto);
    }

    public void sendUserNotFound(ReservationMessageDto reservationMessageDto) {
        send("user.not_found", reservationMessageDto);
    }

    public void sendUserBlackListed(ReservationMessageDto reservationMessageDto) {
        send("user.blacklisted", reservationMessageDto);
    }

    private void send(String routingKey, Object payloadObject) {
        System.out.println("Sending event");
        String payload = objectMapper.writeValueAsString(payloadObject);
        rabbitTemplate.convertAndSend(
                TICKET_RESERVATION_EXCHANGE,
                routingKey,
                payload
        );
    }
}
