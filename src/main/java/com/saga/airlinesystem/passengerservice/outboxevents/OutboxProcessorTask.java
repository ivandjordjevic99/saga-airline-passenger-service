package com.saga.airlinesystem.passengerservice.outboxevents;

import com.saga.airlinesystem.passengerservice.rabbitmq.RabbitProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxProcessorTask {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitProducer rabbitProducer;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void process() {
        List<OutboxEvent> outboxes = outboxEventRepository.findTop10ByStatusOrderByCreatedAtDesc(OutboxEventStatus.PENDING);

        for (OutboxEvent outbox : outboxes) {
            System.out.println("Sending outbox event " + outbox.getRoutingKey() + " on exchange " + outbox.getExchange());
            rabbitProducer.sendEvent(outbox.getExchange(), outbox.getRoutingKey(), outbox.getPayload());
            outbox.setStatus(OutboxEventStatus.SENT);
        }
    }
}
