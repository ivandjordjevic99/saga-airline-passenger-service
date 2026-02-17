package com.saga.airlinesystem.passengerservice.service.impl;

import com.saga.airlinesystem.passengerservice.dto.UserRequestDto;
import com.saga.airlinesystem.passengerservice.dto.UserResponseDto;
import com.saga.airlinesystem.passengerservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.passengerservice.model.User;
import com.saga.airlinesystem.passengerservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.passengerservice.rabbitmq.RabbitProducer;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.passengerservice.repository.UserRepository;
import com.saga.airlinesystem.passengerservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RabbitProducer reservationEventProducer;
    private final OutboxEventService outboxEventService;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setMiles(0);
        user.setBlacklisted(false);
        userRepository.save(user);

        return toDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDto getUserById(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    @Override
    public void checkIfUserIsBlacklisted(ValidateUserRequestMessage message) {
        Optional<User> user = userRepository.findByEmail(message.getEmail());
        UserValidationResultMessage validationResultMessage = new UserValidationResultMessage(message.getReservationId(), message.getEmail());
        if(user.isEmpty()) {
            log.warn("User with email {} not found", message.getEmail());
            validationResultMessage.setResolution("User with email " + message.getEmail() + " not found");
            reservationEventProducer.sendUserValidationFailedEvent(validationResultMessage);
        } else {
            if(user.get().getBlacklisted()) {
                log.warn("User with email {} is blacklisted", message.getEmail());
                validationResultMessage.setResolution("User with email " + message.getEmail() + " is blacklisted");
                reservationEventProducer.sendUserValidationFailedEvent(validationResultMessage);
            } else {
                log.info("User with email {} is validated successfully", message.getEmail());
                validationResultMessage.setResolution("User validation successful");
                reservationEventProducer.sendUserValidatedEvent(validationResultMessage);
            }
        }
    }

    @Override
    @Transactional
    public void updateUserMiles(UpdateUserMilesRequestMessage message) {
        Optional<User> user = userRepository.findByEmail(message.getEmail());
        if(user.isEmpty()) {
            log.warn("User with email {} not found", message.getEmail());
        } else {
            // TODO: Lock
            User userToUpdate = user.get();
            userToUpdate.addMiles(message.getMiles());
            log.info("User with email {} miles updated", message.getEmail());
        }
    }

    private UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setMiles(user.getMiles());
        return dto;
    }
}
