package org.example.park.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.park.model.entity.ParkingSpot;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdateParkingSpotDto {

    @NotNull
    private Long spotId;

    @NotBlank
    private String number;

    @NotNull
    private ParkingSpot.SpotType spotType;

    @NotNull
    private boolean isOccupied;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal hourlyRate;
}
