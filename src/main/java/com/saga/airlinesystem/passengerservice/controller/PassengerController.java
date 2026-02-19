package com.saga.airlinesystem.passengerservice.controller;

import com.saga.airlinesystem.passengerservice.dto.PassengerRequestDto;
import com.saga.airlinesystem.passengerservice.dto.PassengerResponseDto;
import com.saga.airlinesystem.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerResponseDto> create(@RequestBody PassengerRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.createPassenger(dto));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PassengerResponseDto> getById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(passengerService.getPassengerById(uuid));
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponseDto>> getAll() {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }
}
