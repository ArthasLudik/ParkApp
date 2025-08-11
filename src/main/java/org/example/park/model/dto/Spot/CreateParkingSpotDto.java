package org.example.park.model.dto.Spot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.entity.ParkingSpot;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class CreateParkingSpotDto {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 20)
    private String number;

    @NotNull
    private ParkingSpot.SpotType spotType;

    @NotNull
    private boolean isOccupied;


    @NotNull
    @DecimalMin("1.0")
    private BigDecimal hourlyRate;
}
