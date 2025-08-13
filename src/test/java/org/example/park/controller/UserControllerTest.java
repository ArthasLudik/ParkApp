package org.example.park.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.park.model.dto.User.CreateUserDto;
import org.example.park.model.dto.enums.UserRole;
import org.example.park.model.entity.User;
import org.example.park.repository.UserRepository;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setUsername("admin");
        testUser1.setPasswordHash("admin123");
        testUser1.setRole(UserRole.ADMIN);
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setUsername("operator1");
        testUser2.setPasswordHash("op123");
        testUser2.setRole(UserRole.OPERATOR);
        testUser2 = userRepository.save(testUser2);

        testUser3 = new User();
        testUser3.setUsername("operator2");
        testUser3.setPasswordHash("op456");
        testUser3.setRole(UserRole.OPERATOR);
        testUser3 = userRepository.save(testUser3);
    }

    @Test
    void createNewUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("newuser");
        createUserDto.setPasswordHash("password123");
        createUserDto.setRole(UserRole.OPERATOR);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newuser"))
                .andExpect(jsonPath("$.role").value("OPERATOR"));
    }

    @Test
    void createNewUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateUserDto invalidDto = new CreateUserDto();

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {

        mockMvc.perform(get("/api/user/" + testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void updateUserRole_ShouldReturnUpdatedUser() throws Exception {
        mockMvc.perform(patch("/api/user/" + testUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ADMIN\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void deleteUserById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser3.getId()))
                .andExpect(status().isNoContent());
    }
}
