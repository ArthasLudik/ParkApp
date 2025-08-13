package org.example.park.model.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.park.model.dto.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto {

    @NotBlank
    private String username;

    @NotNull
    private String passwordHash;

    @NotNull
    private UserRole role;
}
