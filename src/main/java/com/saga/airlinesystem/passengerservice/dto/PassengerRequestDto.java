package com.saga.airlinesystem.passengerservice.dto;

import lombok.Data;

@Data
public class PassengerRequestDto {

    private String email;
    private String firstName;
    private String lastName;
}
