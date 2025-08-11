package org.example.park.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.entity.Vehicle;

@Getter
@Setter
@AllArgsConstructor
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
    private Vehicle.VehicleType vehicleType;
}
