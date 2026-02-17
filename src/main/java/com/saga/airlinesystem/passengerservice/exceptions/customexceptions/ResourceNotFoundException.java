package com.saga.airlinesystem.passengerservice.exceptions.customexceptions;

public class ResourceNotFoundException extends AbstractException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException() {
        super(404, "Resource not found");
    }
}
