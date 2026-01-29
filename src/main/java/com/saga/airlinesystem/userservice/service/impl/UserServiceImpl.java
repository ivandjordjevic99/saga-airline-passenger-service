package com.saga.airlinesystem.userservice.service.impl;

import com.saga.airlinesystem.userservice.dto.UserRequestDto;
import com.saga.airlinesystem.userservice.dto.UserResponseDto;
import com.saga.airlinesystem.userservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.userservice.model.User;
import com.saga.airlinesystem.userservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.userservice.rabbitmq.RabbitProducer;
import com.saga.airlinesystem.userservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.userservice.rabbitmq.messages.UpdateUserMilesResultMessage;
import com.saga.airlinesystem.userservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.userservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.userservice.repository.UserRepository;
import com.saga.airlinesystem.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.saga.airlinesystem.userservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
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
            validationResultMessage.setResolution("User with email " + message.getEmail() + " not found");
            reservationEventProducer.sendUserValidationFailedEvent(validationResultMessage);
        } else {
            if(user.get().getBlacklisted()) {
                validationResultMessage.setResolution("User with email " + message.getEmail() + " is blacklisted");
                reservationEventProducer.sendUserValidationFailedEvent(validationResultMessage);
            } else {
                validationResultMessage.setResolution("User validation successful");
                reservationEventProducer.sendUserValidatedEvent(validationResultMessage);
            }
        }
    }

    @Override
    @Transactional
    public void updateUserMiles(UpdateUserMilesRequestMessage message) {
        Optional<User> user = userRepository.findByEmail(message.getEmail());
        UpdateUserMilesResultMessage updateUserMilesResultMessage = new UpdateUserMilesResultMessage(message.getReservationId());
        if(user.isEmpty()) {
            updateUserMilesResultMessage.setResolution("User with email " + message.getEmail() + " not found");
            outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, MILES_UPDATE_FAILED_KEY, updateUserMilesResultMessage);
        } else {
            // TODO: Lock
            User userToUpdate = user.get();
            userToUpdate.addMiles(message.getMiles());
            outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, MILES_UPDATED_KEY, updateUserMilesResultMessage);
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
