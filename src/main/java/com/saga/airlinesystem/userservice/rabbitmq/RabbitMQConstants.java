package com.saga.airlinesystem.userservice.rabbitmq;

public class RabbitMQConstants {

    public static final String USER_QUEUE = "user.events";

    public static final String TICKET_RESERVATION_EXCHANGE = "ticket-reservation.exchange";

    public static final String USER_REQUESTS_TOPIC = "request.user.#";

    public static final String USER_VALIDATION_REQUEST_KEY = "request.user.validation";
    public static final String USER_VALIDATED_KEY = "user.validation.validated";
    public static final String USER_VALIDATION_FAILED_KEY = "user.validation.failed";

    public static final String UPDATE_USER_MILES_REQUEST_KEY = "request.user.update-miles";
    public static final String MILES_UPDATED_KEY = "user.miles.updated";
    public static final String MILES_UPDATE_FAILED_KEY = "user.miles.update-failed";
}
