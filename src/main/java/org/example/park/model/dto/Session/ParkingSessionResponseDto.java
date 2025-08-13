package org.example.park.model.dto.Session;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private Long sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime exitTime;

    @NotNull
    @Positive
    private Long spotId;

    @NotNull
    @Positive
    private Long vehicleId;

    @NotNull
    @Positive
    private Long operatorId;

    private BigDecimal paidAmount;

}
