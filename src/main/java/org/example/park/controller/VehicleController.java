package org.example.park.controller;

import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.Vehicle.CreateVehicleDto;
import org.example.park.model.dto.Vehicle.VehicleResponseDto;
import org.example.park.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDto> createNewVehicle(@RequestBody CreateVehicleDto dto) {
        return ResponseEntity.ok(vehicleService.createNewVehicle(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleById(@PathVariable Long id) {
        vehicleService.deleteVehicleById(id);
        return ResponseEntity.noContent().build();
    }
}
