package com.saga.airlinesystem.userservice.service;

import com.saga.airlinesystem.userservice.dto.UserRequestDto;
import com.saga.airlinesystem.userservice.dto.UserResponseDto;
import com.saga.airlinesystem.userservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.userservice.rabbitmq.messages.ValidateUserRequestMessage;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(UUID id);
    void checkIfUserIsBlacklisted(ValidateUserRequestMessage message);
    void updateUserMiles(UpdateUserMilesRequestMessage message);
}
