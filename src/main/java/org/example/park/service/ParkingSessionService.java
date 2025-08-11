package org.example.park.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.park.model.entity.ParkingSpot;
import org.example.park.model.entity.Vehicle;
import org.example.park.repository.ParkingSpotRepository;
import org.example.park.repository.VehicleRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.Session.CompleteParkingSessionDto;
import org.example.park.model.dto.Session.CreateParkingSessionDto;
import org.example.park.model.dto.Session.ParkingSessionResponseDto;
import org.example.park.model.entity.ParkingSession;
import org.example.park.repository.ParkingSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {
    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public ParkingSessionResponseDto createSession(CreateParkingSessionDto dto) {
        ParkingSpot spot = parkingSpotRepository.findById(dto.getSpotId()).orElseThrow(()
        -> new EntityNotFoundException("Нет места с таким Id"));

        if (spot.isOccupied()) {
            throw new IllegalStateException("Место занято");
        }

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicle().getId()).orElseThrow(()
        -> new EntityNotFoundException("Нет машины с таким Id"));

        ParkingSession session = new ParkingSession();
        session.setSpot(spot);
        session.setVehicle(vehicle);

        spot.setOccupied(true);

        ParkingSession savedSession = parkingSessionRepository.save(session);

        return new ParkingSessionResponseDto(
                savedSession.getId(),
                savedSession.getEntryTime(),
                spot.getId(),
                vehicle.getId(),
                session.getTotalCost()
        );
    }

    public void deleteSessionById(Long id) {
        ParkingSession session = parkingSessionRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Сессия с таким id не найдена"));

        if (session.getExitTime() == null) {
            ParkingSpot spot = session.getSpot();
            spot.setOccupied(false);
            parkingSpotRepository.save(spot);
        }

        parkingSessionRepository.delete(session);
    }

    @Transactional
    public ParkingSessionResponseDto completeParkingSession(CompleteParkingSessionDto dto) {
        ParkingSession session = parkingSessionRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Сессия c таким id не найдена"));

        if (session.getExitTime() != null) {
            throw new IllegalStateException("Сессия уже завершена");
        }

        session.setExitTime(LocalDateTime.now());

        ParkingSpot spot = session.getSpot();
        spot.setOccupied(false);

        ParkingSession save = parkingSessionRepository.save(session);

        return new ParkingSessionResponseDto(
                save.getId(),
                save.getEntryTime(),
                spot.getId(),
                save.getVehicle().getId(),
                save.getTotalCost()
        );
    }

    @Transactional(readOnly = true)
    public List<ParkingSessionResponseDto> getAllParkingSession() {
        return parkingSessionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParkingSessionResponseDto getParkingSessionById(Long id) {
        return parkingSessionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Сессия не найдена"));
    }

    private ParkingSessionResponseDto convertToDto(ParkingSession session) {
        return new ParkingSessionResponseDto(
                session.getId(),
                session.getEntryTime(),
                session.getSpot().getId(),
                session.getVehicle().getId(),
                session.getTotalCost()
        );
    }
}
