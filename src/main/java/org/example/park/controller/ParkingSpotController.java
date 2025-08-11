package org.example.park.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.Spot.CreateParkingSpotDto;
import org.example.park.model.dto.Spot.ParkingSpotResponseDto;
import org.example.park.model.dto.Spot.UpdateParkingSpotDto;
import org.example.park.service.ParkingSpotService;
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
@RequestMapping("api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;


    @PostMapping
    public ResponseEntity<ParkingSpotResponseDto> createParkingSpot(
            @RequestBody CreateParkingSpotDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.createSpot(createDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingSpotByNumber(@PathVariable Long id) {
        parkingSpotService.deleteSpotById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ParkingSpotResponseDto> updateParkingSpot(@RequestBody @Valid UpdateParkingSpotDto updateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.updateSpot(updateDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotResponseDto> getParkingSpotByNumber(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpotService.getSpotById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ParkingSpotResponseDto>> getAllParkingSpots(){
        return ResponseEntity.ok(parkingSpotService.getAllSpots());
    }

    @GetMapping("/free")
    public ResponseEntity<List<ParkingSpotResponseDto>> getFreeSpots() {
        return ResponseEntity.ok(parkingSpotService.findFreeSpots());
    }

}
