package com.tresorshautebretagne.userProgress;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProximityCheckRequest {

    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;
}
