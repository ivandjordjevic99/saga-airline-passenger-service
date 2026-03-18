package com.saga.airlinesystem.passengerservice.inboxevents;

import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import com.saga.airlinesystem.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxProcessorTask {

    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;
    private final PassengerService passengerService;

    @Scheduled(fixedDelay = 1000)
    public void process() {
        List<InboxEvent> inboxEvents = inboxEventRepository.findTop10ByStatusOrderByReceivedAtAsc(InboxEventStatus.PENDING);

        for (InboxEvent inboxEvent : inboxEvents) {
            inboxEvent.setStatus(InboxEventStatus.IN_PROGRESS);
            inboxEventRepository.save(inboxEvent);
            handleInboxEvent(inboxEvent);
            inboxEvent.setStatus(InboxEventStatus.PROCESSED);
            inboxEventRepository.save(inboxEvent);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void processStuckEvents() {
        List<InboxEvent> stuckEvents = inboxEventRepository.findByStatusAndUpdatedAtBefore(
                InboxEventStatus.IN_PROGRESS,
                OffsetDateTime.now().minusMinutes(3)
        );
        for (InboxEvent stuckEvent : stuckEvents) {
            if(stuckEvent.getRetryCount() < 5) {
                stuckEvent.setStatus(InboxEventStatus.PENDING);
                stuckEvent.incrementRetryCount();
                inboxEventRepository.save(stuckEvent);
                log.info("{} message moved to PENDING, retry count: {}", stuckEvent.getMessageId(), stuckEvent.getRetryCount());
            } else {
                stuckEvent.setStatus(InboxEventStatus.FAILED);
                inboxEventRepository.save(stuckEvent);
                log.error("{} message couldn't be processed after 5 times", stuckEvent.getMessageId());
            }
        }
    }

    private void handleInboxEvent(InboxEvent inboxEvent) {
        InboxEventType inboxEventType = inboxEvent.getInboxEventType();
        String payload = inboxEvent.getPayload();

        switch (inboxEventType) {
            case PASSENGER_VALIDATION_REQUEST:
                handlePassengerValidationRequest(payload);
                break;
            case PASSENGER_UPDATE_MILES_REQUEST:
                handleUpdatePassengerMilesRequest(payload);
                break;
            default:
                log.error("Invalid inbox event type: {}", inboxEventType);
        }
    }

    private void handlePassengerValidationRequest(String payload) {
        ValidatePassengerRequestMessage message = objectMapper.readValue(payload, ValidatePassengerRequestMessage.class);
        passengerService.checkIfPassengerIsBlacklisted(message);
    }

    private void handleUpdatePassengerMilesRequest(String payload) {
        UpdatePassengerMilesRequestMessage message = objectMapper.readValue(payload, UpdatePassengerMilesRequestMessage.class);
        passengerService.updatePassengerMiles(message);
    }
}
