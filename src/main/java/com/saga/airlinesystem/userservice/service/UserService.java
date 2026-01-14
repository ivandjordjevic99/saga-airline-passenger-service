package com.saga.airlinesystem.userservice.service;

import com.saga.airlinesystem.userservice.dto.ReservationMessageDto;
import com.saga.airlinesystem.userservice.dto.UserRequestDto;
import com.saga.airlinesystem.userservice.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(UUID id);
    void checkIfUserIsBlacklisted(ReservationMessageDto reservationMessageDto);
}
