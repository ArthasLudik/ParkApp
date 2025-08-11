package org.example.park.controller;

import lombok.RequiredArgsConstructor;
import org.example.park.model.dto.User.CreateUserDto;
import org.example.park.model.dto.User.UserResponseDto;
import org.example.park.model.entity.User;
import org.example.park.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(userService.createNewUser(createUserDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long id, @RequestBody User.Role role) {
        return ResponseEntity.ok(userService.updateUserRoleById(id, role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
