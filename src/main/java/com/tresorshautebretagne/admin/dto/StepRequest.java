package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StepRequest {
    @NotNull(message = "L'ordre de l'étape est obligatoire")
    private Integer stepOrder;
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    private String description;
    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;
    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;
    private Integer radiusMeters = 50;
}
