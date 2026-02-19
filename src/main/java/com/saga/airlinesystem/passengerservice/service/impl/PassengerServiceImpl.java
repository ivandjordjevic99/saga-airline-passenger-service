package com.saga.airlinesystem.passengerservice.service.impl;

import com.saga.airlinesystem.passengerservice.dto.PassengerRequestDto;
import com.saga.airlinesystem.passengerservice.dto.PassengerResponseDto;
import com.saga.airlinesystem.passengerservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.passengerservice.model.Passenger;
import com.saga.airlinesystem.passengerservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.passengerservice.rabbitmq.RabbitProducer;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final RabbitProducer rabbitProducer;
    private final OutboxEventService outboxEventService;

    @Override
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = new Passenger();
        passenger.setFirstName(passengerRequestDto.getFirstName());
        passenger.setLastName(passengerRequestDto.getLastName());
        passenger.setEmail(passengerRequestDto.getEmail());
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
    public void checkIfPassengerIsBlacklisted(ValidatePassengerRequestMessage message) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(message.getEmail());
        PassengerValidationResultMessage validationResultMessage = new PassengerValidationResultMessage(message.getTicketOrderId(), message.getEmail());
        if(passenger.isEmpty()) {
            log.warn("Passenger with email {} not found", message.getEmail());
            validationResultMessage.setResolution("Passenger with email " + message.getEmail() + " not found");
            rabbitProducer.sendPassengerValidationFailedEvent(validationResultMessage);
        } else {
            if(passenger.get().getBlacklisted()) {
                log.warn("Passenger with email {} is blacklisted", message.getEmail());
                validationResultMessage.setResolution("Passenger with email " + message.getEmail() + " is blacklisted");
                rabbitProducer.sendPassengerValidationFailedEvent(validationResultMessage);
            } else {
                log.info("Passenger with email {} is validated successfully", message.getEmail());
                validationResultMessage.setResolution("Passenger validation successful");
                rabbitProducer.sendPassengerValidatedEvent(validationResultMessage);
            }
        }
    }

    @Override
    @Transactional
    public void updatePassengerMiles(UpdatePassengerMilesRequestMessage message) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(message.getEmail());
        if(passenger.isEmpty()) {
            log.warn("Passenger with email {} not found", message.getEmail());
        } else {
            // TODO: Lock
            Passenger passengerToUpdate = passenger.get();
            passengerToUpdate.addMiles(message.getMiles());
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
