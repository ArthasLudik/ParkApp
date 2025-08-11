package org.example.park.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.park.model.dto.User.CreateUserDto;
import org.example.park.model.dto.User.UserResponseDto;
import org.example.park.model.entity.User;
import org.example.park.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createNewUser(CreateUserDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не существует"));

        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPasswordHash());
        user.setRole(dto.getRole());

        User savedUser = userRepository.save(user);
        return new UserResponseDto(
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Нет такого пользователя"));

        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDto updateUserRoleById(Long id, User.Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден"));
        user.setRole(role);
        return new UserResponseDto(
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return new UserResponseDto(
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getRole()
        );
    }

}
