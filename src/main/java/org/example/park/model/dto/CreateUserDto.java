package org.example.park.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.park.model.entity.User;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto {
   @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    private String passwordHash;

    @NotNull
    private User.Role role;
}
