package com.saga.airlinesystem.userservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ValidateUserRequestMessage extends BaseMessage {

    private final String reservationId;
    private final String email;
}
