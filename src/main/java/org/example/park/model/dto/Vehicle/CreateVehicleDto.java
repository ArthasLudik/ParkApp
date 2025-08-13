package org.example.park.model.dto.Vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.park.model.dto.enums.VehicleType;

@Getter
@Setter
@NoArgsConstructor
public class CreateVehicleDto {

    @NotNull
    private Long id;

    @NotBlank
    private String licensePlate;

    @Size(max = 50)
    private String model;

    @Size(max = 20)
    private String color;

    @NotNull
    private VehicleType vehicleType;
}
