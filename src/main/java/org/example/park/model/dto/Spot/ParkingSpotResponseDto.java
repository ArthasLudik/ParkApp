package org.example.park.model.dto.Spot;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.dto.enums.SpotType;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ParkingSpotResponseDto {

    @NotNull
    @Positive
    private Long spotId;

    @JsonProperty("number")
    @NotNull
    private String spotNumber;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SpotType type;

    @NotBlank
    private boolean isOccupied;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#0.00")
    private BigDecimal hourlyRate;

    @JsonProperty("status")
    public String getStatus() {
        return isOccupied ? "OCCUPIED" : "FREE";
    }
}
