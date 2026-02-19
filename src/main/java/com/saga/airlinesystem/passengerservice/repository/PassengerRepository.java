package com.saga.airlinesystem.passengerservice.repository;

import com.saga.airlinesystem.passengerservice.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    Optional<Passenger> findByEmail(String email);
}
