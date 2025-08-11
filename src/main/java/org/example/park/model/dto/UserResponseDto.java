package org.example.park.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.entity.User;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    @NotNull
    private String name;

    @NotNull
    private User.Role role;
}
