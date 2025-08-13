package org.example.park.model.dto.Session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompleteParkingSessionDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Positive
    private Long sessionId;

}
