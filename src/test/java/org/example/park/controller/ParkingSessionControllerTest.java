package org.example.park.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.park.model.dto.Session.CompleteParkingSessionDto;
import org.example.park.model.dto.Session.CreateParkingSessionDto;
import org.example.park.model.dto.enums.SpotType;
import org.example.park.model.dto.enums.VehicleType;
import org.example.park.model.entity.ParkingSession;
import org.example.park.model.entity.ParkingSpot;
import org.example.park.model.entity.Vehicle;
import org.example.park.repository.ParkingSessionRepository;
import org.example.park.repository.ParkingSpotRepository;
import org.example.park.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ParkingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    private ParkingSpot freeSpot;
    private ParkingSpot occupiedSpot;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private ParkingSession existingSession;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        spotRepository.deleteAll();
        vehicleRepository.deleteAll();

        vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("XYZ111");
        vehicle1.setModel("Honda Civic");
        vehicle1.setColor("Blue");
        vehicle1.setType(VehicleType.CAR);
        vehicle1 = vehicleRepository.save(vehicle1);

        vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("XYZ222");
        vehicle2.setModel("Ford F-150");
        vehicle2.setColor("White");
        vehicle2.setType(VehicleType.TRUCK);
        vehicle2 = vehicleRepository.save(vehicle2);

        freeSpot = new ParkingSpot();
        freeSpot.setNumber("S1");
        freeSpot.setSpotType(SpotType.STANDARD);
        freeSpot.setOccupied(false);
        freeSpot.setHourlyRate(java.math.BigDecimal.valueOf(5.00));
        freeSpot = spotRepository.save(freeSpot);

        occupiedSpot = new ParkingSpot();
        occupiedSpot.setNumber("S2");
        occupiedSpot.setSpotType(SpotType.STANDARD);
        occupiedSpot.setOccupied(true);
        occupiedSpot.setHourlyRate(java.math.BigDecimal.valueOf(5.00));
        occupiedSpot = spotRepository.save(occupiedSpot);

        existingSession = new ParkingSession();
        existingSession.setSpot(occupiedSpot);
        existingSession.setVehicle(vehicle1);
        existingSession.setOperatorId(2L);
        existingSession = sessionRepository.save(existingSession);
    }

    @Test
    void getAllSession_ShouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void getParkingSession_ShouldReturnSession() throws Exception {
        mockMvc.perform(get("/api/session/" + existingSession.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(existingSession.getId().intValue()))
                .andExpect(jsonPath("$.vehicleId").value(vehicle1.getId().intValue()))
                .andExpect(jsonPath("$.spotId").value(occupiedSpot.getId().intValue()));
    }

    @Test
    void createSession_ShouldReturnCreatedSession() throws Exception {
        CreateParkingSessionDto createDto = new CreateParkingSessionDto();
        createDto.setId(999L);
        createDto.setVehicleId(vehicle2.getId());
        createDto.setSpotId(freeSpot.getId());
        createDto.setOperatorId(2L);
        createDto.setEntryTime(LocalDateTime.now().plusSeconds(1));

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleId").value(vehicle2.getId().intValue()))
                .andExpect(jsonPath("$.spotId").value(freeSpot.getId().intValue()));
    }

    @Test
    void createSession_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateParkingSessionDto invalidDto = new CreateParkingSessionDto();

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeSession_ShouldReturnCompletedSession() throws Exception {
        CompleteParkingSessionDto completeDto = new CompleteParkingSessionDto();
        completeDto.setId(existingSession.getId());
        completeDto.setSessionId(existingSession.getId());

        mockMvc.perform(put("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitTime").exists());
    }

    @Test
    void deleteSessionById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/session/" + existingSession.getId()))
                .andExpect(status().isNoContent());
    }
}
