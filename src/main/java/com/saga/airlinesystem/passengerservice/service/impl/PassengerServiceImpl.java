package com.saga.airlinesystem.passengerservice.service.impl;

import com.saga.airlinesystem.passengerservice.dto.PassengerRequestDto;
import com.saga.airlinesystem.passengerservice.dto.PassengerResponseDto;
import com.saga.airlinesystem.passengerservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.passengerservice.model.Passenger;
import com.saga.airlinesystem.passengerservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.PassengerValidationResultMessage;
import com.saga.airlinesystem.passengerservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import com.saga.airlinesystem.passengerservice.repository.PassengerRepository;
import com.saga.airlinesystem.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.saga.airlinesystem.passengerservice.rabbitmq.RabbitMQConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final OutboxEventService outboxEventService;

    @Override
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = new Passenger();
        passenger.setEmail(passengerRequestDto.getEmail());
        passenger.setFirstName(passengerRequestDto.getFirstName());
        passenger.setLastName(passengerRequestDto.getLastName());
        passenger.setMiles(0);
        passenger.setBlacklisted(false);
        passengerRepository.save(passenger);

        return toDto(passenger);
    }

    @Override
    public List<PassengerResponseDto> getAllPassengers() {
        return passengerRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public PassengerResponseDto getPassengerById(UUID uuid) {
        Passenger passenger = passengerRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
        return toDto(passenger);
    }

    @Override
    @Transactional
    public void checkIfPassengerIsBlacklisted(ValidatePassengerRequestMessage message) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(message.getEmail());
        PassengerValidationResultMessage validationResultMessage = new PassengerValidationResultMessage(message.getTicketOrderId(), message.getEmail());
        if(passenger.isEmpty()) {
            log.info("Passenger with email {} not found. Checking if blacklisted", message.getEmail());
            boolean isBlacklisted = Math.random() < 0.05;
            if(isBlacklisted) {
                log.info("Passenger with email {} is blacklisted", message.getEmail());
                validationResultMessage.setResolution("Passenger with email " + message.getEmail() + " is blacklisted");
                outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATION_FAILED_KEY, validationResultMessage);
            } else {
                log.info("Passenger with email {} is validated successfully", message.getEmail());
                validationResultMessage.setResolution("Passenger validation successful");
                outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATED_KEY, validationResultMessage);
            }
        } else {
            if(passenger.get().getBlacklisted()) {
                log.info("Passenger with email {} is blacklisted", message.getEmail());
                validationResultMessage.setResolution("Passenger with email " + message.getEmail() + " is blacklisted");
                outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATION_FAILED_KEY, validationResultMessage);
            } else {
                log.info("Passenger with email {} is validated successfully", message.getEmail());
                validationResultMessage.setResolution("Passenger validation successful");
                outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATED_KEY, validationResultMessage);
            }
        }
    }

    @Override
    @Transactional
    public void updatePassengerMiles(UpdatePassengerMilesRequestMessage message) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(message.getEmail());
        if(passenger.isEmpty()) {
            log.warn("Passenger with email {} not found. Passenger is a guest user and miles won't be added", message.getEmail());
        } else {
            Passenger passengerToUpdate = passenger.get();
            passengerRepository.addMiles(passengerToUpdate.getId(), message.getMiles());
            log.info("Passenger with email {} miles updated", message.getEmail());
        }
    }

    private PassengerResponseDto toDto(Passenger passenger) {
        PassengerResponseDto dto = new PassengerResponseDto();
        dto.setId(passenger.getId());
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setMiles(passenger.getMiles());
        return dto;
    }
}
