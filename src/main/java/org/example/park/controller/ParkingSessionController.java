package org.example.park.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.CompleteParkingSessionDto;
import org.example.park.model.dto.CreateParkingSessionDto;
import org.example.park.model.dto.ParkingSessionResponseDto;
import org.example.park.service.ParkingSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/session")
@RequiredArgsConstructor
public class ParkingSessionController {
    private final ParkingSessionService parkingSessionService;

    @PostMapping
    public ResponseEntity<ParkingSessionResponseDto> post(@RequestBody @Valid CreateParkingSessionDto createDto) {
        ParkingSessionResponseDto responseDto = parkingSessionService.createSession(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSessionResponseDto> getParkingSession(@PathVariable Long id) {
        ParkingSessionResponseDto session = parkingSessionService.getParkingSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<ParkingSessionResponseDto>> getAllSession() {
        List<ParkingSessionResponseDto> sessions = parkingSessionService.getAllParkingSession();
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingSessionService.deleteSessionById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ParkingSessionResponseDto> put(@RequestBody CompleteParkingSessionDto completeDto) {
        ParkingSessionResponseDto responseDto = parkingSessionService.completeParkingSession(completeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
