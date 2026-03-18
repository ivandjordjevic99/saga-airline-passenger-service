package com.saga.airlinesystem.passengerservice.exceptions.customexceptions;

import lombok.Getter;

@Getter
public class EventAlreadyReceivedException extends RuntimeException {

    private final String message;

    public EventAlreadyReceivedException(String message) {
        this.message = message;
    }

}
