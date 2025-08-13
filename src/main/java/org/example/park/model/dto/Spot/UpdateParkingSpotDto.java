package org.example.park.model.dto.Spot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.park.model.dto.enums.SpotType;
import org.example.park.model.entity.ParkingSpot;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdateParkingSpotDto {

    @NotNull
    @Positive
    private Long spotId;

    @NotBlank
    private String number;

    @NotNull
    private SpotType spotType;

    @NotNull
    private boolean isOccupied;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal hourlyRate;
}
