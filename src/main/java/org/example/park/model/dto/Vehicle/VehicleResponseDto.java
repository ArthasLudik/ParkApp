package org.example.park.model.dto.Vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VehicleResponseDto {

    @NotNull
    private Long id;

    @NotNull
    private String model;

    @NotBlank
    private String licensePlate;
}
