package org.example.park.model.dto.Session;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ParkingSessionResponseDto {
    @NotNull
    private Long sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entryTime;

    @NotNull
    private Long spotId;

    @NotNull
    private Long vehicleId;


    private BigDecimal paidAmount;

}
