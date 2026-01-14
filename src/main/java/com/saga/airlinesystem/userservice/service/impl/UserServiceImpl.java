package com.saga.airlinesystem.userservice.service.impl;

import com.saga.airlinesystem.userservice.dto.ReservationMessageDto;
import com.saga.airlinesystem.userservice.dto.UserRequestDto;
import com.saga.airlinesystem.userservice.dto.UserResponseDto;
import com.saga.airlinesystem.userservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.userservice.model.User;
import com.saga.airlinesystem.userservice.rabbitmq.RabbitProducer;
import com.saga.airlinesystem.userservice.repository.UserRepository;
import com.saga.airlinesystem.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RabbitProducer reservationEventProducer;

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
    public void checkIfUserIsBlacklisted(ReservationMessageDto reservationMessageDto) {
        Optional<User> user = userRepository.findByEmail(reservationMessageDto.getEmail());
        if(user.isEmpty()) {
            System.out.println("Saljem not found");
            reservationEventProducer.sendUserNotFound(reservationMessageDto);
        } else {
            if(user.get().getBlacklisted()) {
                System.out.println("Saljem blacklisted");
                reservationEventProducer.sendUserBlackListed(reservationMessageDto);
            } else {
                System.out.println("Saljem ok");
                reservationEventProducer.sendUserOk(reservationMessageDto);
            }
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
