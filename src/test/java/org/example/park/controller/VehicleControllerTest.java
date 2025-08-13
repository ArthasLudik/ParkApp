package org.example.park.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.park.model.dto.Vehicle.CreateVehicleDto;
import org.example.park.model.dto.enums.VehicleType;
import org.example.park.model.entity.Vehicle;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle existing;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        existing = new Vehicle();
        existing.setLicensePlate("ABC123");
        existing.setModel("Toyota Camry");
        existing.setColor("Red");
        existing.setType(VehicleType.CAR);
        existing = vehicleRepository.save(existing);
    }

    @Test
    void createNewVehicle_ShouldReturnCreatedVehicle() throws Exception {
        CreateVehicleDto createDto = new CreateVehicleDto();
        createDto.setId(999L);
        createDto.setLicensePlate("NEW123");
        createDto.setModel("Tesla Model 3");
        createDto.setColor("Silver");
        createDto.setVehicleType(VehicleType.CAR);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("NEW123"))
                .andExpect(jsonPath("$.model").value("Tesla Model 3"));
    }

    @Test
    void createNewVehicle_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateVehicleDto invalidDto = new CreateVehicleDto();

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVehicleById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/vehicles/" + existing.getId()))
                .andExpect(status().isNoContent());
    }
}
