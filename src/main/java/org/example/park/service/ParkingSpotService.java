package org.example.park.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.CreateParkingSpotDto;
import org.example.park.model.dto.ParkingSpotResponseDto;
import org.example.park.model.dto.UpdateParkingSpotDto;
import org.example.park.model.entity.ParkingSpot;
import org.example.park.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;

    @Transactional
    public ParkingSpotResponseDto createSpot(CreateParkingSpotDto dto) {
        ParkingSpot spot = new ParkingSpot();

        spot.setSpotType(dto.getSpotType());
        spot.setHourlyRate(dto.getHourlyRate());
        spot.setNumber(dto.getNumber());
        spot.setOccupied(dto.isOccupied());

        ParkingSpot saved = parkingSpotRepository.save(spot);

        return new ParkingSpotResponseDto(
                saved.getId(),
                saved.getNumber(),
                saved.getSpotType(),
                saved.isOccupied(),
                saved.getHourlyRate()
        );
    }

    @Transactional
    public ParkingSpotResponseDto updateSpot(UpdateParkingSpotDto dto) {
        ParkingSpot spot = parkingSpotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new EntityNotFoundException("Места с таким id не найдена"));

        spot.setOccupied(dto.isOccupied());
        spot.setSpotType(dto.getSpotType());
        spot.setNumber(dto.getNumber());
        spot.setHourlyRate(dto.getHourlyRate());

        ParkingSpot saved = parkingSpotRepository.save(spot);

        return new ParkingSpotResponseDto(
                saved.getId(),
                saved.getNumber(),
                saved.getSpotType(),
                saved.isOccupied(),
                saved.getHourlyRate()
        );
    }

    @Transactional
    public void deleteSpotById(Long id) {
        ParkingSpot spot = parkingSpotRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Нет места с таким номером"));

        if(spot.isOccupied()) {
            throw new IllegalStateException("Место используется в сессии");
        }

        parkingSpotRepository.delete(spot);
    }

    @Transactional(readOnly = true)
    public ParkingSpotResponseDto getSpotById(Long id) {
        return parkingSpotRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Нет места с таким id"));
    }

    @Transactional(readOnly = true)
    public List<ParkingSpotResponseDto> getAllSpots() {
        return parkingSpotRepository
                .findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParkingSpotResponseDto> findFreeSpots() {
        return parkingSpotRepository.findByIsOccupiedFalse()
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ParkingSpotResponseDto convertToDto(ParkingSpot spot) {
        return new ParkingSpotResponseDto(
                spot.getId(),
                spot.getNumber(),
                spot.getSpotType(),
                spot.isOccupied(),
                spot.getHourlyRate()
        );
    }
}
