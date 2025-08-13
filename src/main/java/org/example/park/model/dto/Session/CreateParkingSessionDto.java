package org.example.park.model.dto.Session;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateParkingSessionDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private Long vehicleId;

    @FutureOrPresent
    private LocalDateTime entryTime;

    @NotNull
    @Positive
    private Long spotId;
    
    @NotNull
    @Positive
    private Long operatorId;
    
}
