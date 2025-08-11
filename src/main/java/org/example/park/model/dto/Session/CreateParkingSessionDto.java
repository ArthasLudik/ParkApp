package org.example.park.model.dto.Session;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateParkingSessionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long vehicleId;

    @FutureOrPresent
    private LocalDateTime entryTime;

    @NotNull
    @Positive
    private Long spotId;
}
