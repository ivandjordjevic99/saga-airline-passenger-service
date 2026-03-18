package com.saga.airlinesystem.passengerservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PassengerResponseDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer miles;
    private boolean blacklisted;
}
