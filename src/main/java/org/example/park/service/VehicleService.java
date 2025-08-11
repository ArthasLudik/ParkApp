package org.example.park.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.Vehicle.CreateVehicleDto;
import org.example.park.model.dto.Vehicle.VehicleResponseDto;
import org.example.park.model.entity.Vehicle;
import org.example.park.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleResponseDto createNewVehicle(CreateVehicleDto dto) {
        Vehicle vehicle = new Vehicle();

        vehicle.setType(dto.getVehicleType());
        vehicle.setModel(dto.getModel());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setColor(dto.getColor());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return new VehicleResponseDto(
                savedVehicle.getId(),
                savedVehicle.getModel(),
                savedVehicle.getLicensePlate()
        );
    }

    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Нет машины с таким id"));

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponseDto convertToDto(Vehicle vehicle) {
        return new VehicleResponseDto(
                vehicle.getId(),
                vehicle.getModel(),
                vehicle.getLicensePlate()
        );
    }


}
