package org.example.park.model.dto.Session;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompleteParkingSessionDto {

    @NotNull
    private Long id;

}
