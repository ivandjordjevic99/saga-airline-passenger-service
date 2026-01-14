package com.saga.airlinesystem.userservice.repository;

import com.saga.airlinesystem.userservice.dto.UserResponseDto;
import com.saga.airlinesystem.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
