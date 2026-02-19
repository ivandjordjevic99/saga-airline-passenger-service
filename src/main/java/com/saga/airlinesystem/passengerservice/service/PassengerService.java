package com.saga.airlinesystem.passengerservice.service;

import com.saga.airlinesystem.passengerservice.dto.PassengerRequestDto;
import com.saga.airlinesystem.passengerservice.dto.PassengerResponseDto;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidatePassengerRequestMessage;

import java.util.List;
import java.util.UUID;

public interface PassengerService {

    PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto);
    List<PassengerResponseDto> getAllPassengers();
    PassengerResponseDto getPassengerById(UUID id);
    void checkIfPassengerIsBlacklisted(ValidatePassengerRequestMessage message);
    void updatePassengerMiles(UpdatePassengerMilesRequestMessage message);
}
