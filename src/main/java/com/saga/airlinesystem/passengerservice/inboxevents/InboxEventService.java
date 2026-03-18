package com.saga.airlinesystem.passengerservice.inboxevents;

import java.util.List;
import java.util.UUID;

public interface InboxEventService {

    void saveInboxEvent(UUID messageId, String payload, InboxEventType inboxEventType);
}
