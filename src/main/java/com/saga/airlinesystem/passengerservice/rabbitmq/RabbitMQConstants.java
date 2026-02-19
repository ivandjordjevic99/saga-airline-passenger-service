package com.saga.airlinesystem.passengerservice.rabbitmq;

public class RabbitMQConstants {

    // queue
    public static final String PASSENGER_QUEUE = "passenger.events";

    // exchanges
    public static final String TICKET_BOOKING_EXCHANGE = "ticket-booking.exchange";

    public static final String PASSENGER_REQUESTS_TOPIC = "request.passenger.#";

    public static final String PASSENGER_VALIDATION_REQUEST_KEY = "request.passenger.validation";
    public static final String PASSENGER_VALIDATED_KEY = "passenger.validation.validated";
    public static final String PASSENGER_VALIDATION_FAILED_KEY = "passenger.validation.failed";

    public static final String UPDATE_PASSENGER_MILES_REQUEST_KEY = "request.passenger.update-miles";
}
