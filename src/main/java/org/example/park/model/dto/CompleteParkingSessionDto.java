package org.example.park.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CompleteParkingSessionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long spotId;

    @NotNull
    private LocalDateTime exitTime;

    private BigDecimal paidAmount;

}
