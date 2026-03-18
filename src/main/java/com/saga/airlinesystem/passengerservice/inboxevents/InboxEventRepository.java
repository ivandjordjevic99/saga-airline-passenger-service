package com.saga.airlinesystem.passengerservice.inboxevents;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {

    List<InboxEvent> findTop10ByStatusOrderByReceivedAtAsc(InboxEventStatus status);
    List<InboxEvent> findByStatusAndUpdatedAtBefore(InboxEventStatus status, OffsetDateTime time);

}
