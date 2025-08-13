package org.example.park.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.park.model.dto.Spot.CreateParkingSpotDto;
import org.example.park.model.dto.Spot.UpdateParkingSpotDto;
import org.example.park.model.dto.enums.SpotType;
import org.example.park.model.entity.ParkingSpot;
import org.example.park.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private ParkingSpot spot1;
    private ParkingSpot spot2;

    @BeforeEach
    void setUp() {
        parkingSpotRepository.deleteAll();

        spot1 = new ParkingSpot();
        spot1.setNumber("A1");
        spot1.setSpotType(SpotType.STANDARD);
        spot1.setOccupied(false);
        spot1.setHourlyRate(new BigDecimal("5.00"));
        spot1 = parkingSpotRepository.save(spot1);

        spot2 = new ParkingSpot();
        spot2.setNumber("A2");
        spot2.setSpotType(SpotType.ELECTRIC);
        spot2.setOccupied(false);
        spot2.setHourlyRate(new BigDecimal("7.50"));
        spot2 = parkingSpotRepository.save(spot2);
    }

    @Test
    void getAllParkingSpots_ShouldReturnAllSpots() throws Exception {
        mockMvc.perform(get("/api/spots/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void getFreeSpots_ShouldReturnFreeSpots() throws Exception {
        mockMvc.perform(get("/api/spots/free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void getParkingSpotById_ShouldReturnSpot() throws Exception {
        mockMvc.perform(get("/api/spots/" + spot1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("A1"))
                .andExpect(jsonPath("$.type").value("STANDARD"));
    }

    @Test
    void createParkingSpot_ShouldReturnCreatedSpot() throws Exception {
        CreateParkingSpotDto createDto = new CreateParkingSpotDto(999L, "B1", SpotType.STANDARD, false, new BigDecimal("6.00"));

        mockMvc.perform(post("/api/spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("B1"))
                .andExpect(jsonPath("$.type").value("STANDARD"));
    }

    @Test
    void createParkingSpot_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateParkingSpotDto invalidDto = new CreateParkingSpotDto(null, null, null, false, null);

        mockMvc.perform(post("/api/spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateParkingSpot_ShouldReturnUpdatedSpot() throws Exception {
        UpdateParkingSpotDto updateDto = new UpdateParkingSpotDto();
        updateDto.setSpotId(spot1.getId());
        updateDto.setNumber("A1");
        updateDto.setSpotType(SpotType.STANDARD);
        updateDto.setOccupied(false);
        updateDto.setHourlyRate(new BigDecimal("6.00"));

        mockMvc.perform(put("/api/spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hourlyRate").value("6.00"));
    }

    @Test
    void deleteParkingSpotById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/spots/" + spot2.getId()))
                .andExpect(status().isNoContent());
    }
}
