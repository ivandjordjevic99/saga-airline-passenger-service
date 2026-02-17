package com.saga.airlinesystem.passengerservice.service;

import com.saga.airlinesystem.passengerservice.dto.UserRequestDto;
import com.saga.airlinesystem.passengerservice.dto.UserResponseDto;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidateUserRequestMessage;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(UUID id);
    void checkIfUserIsBlacklisted(ValidateUserRequestMessage message);
    void updateUserMiles(UpdateUserMilesRequestMessage message);
}
