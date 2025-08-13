package org.example.park.model.dto.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.dto.enums.UserRole;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private UserRole role;
}
