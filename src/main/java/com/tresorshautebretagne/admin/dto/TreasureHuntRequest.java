package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreasureHuntRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    private String description;
    @NotNull(message = "Le thème est obligatoire")
    private Long themeId;
    @NotNull(message = "La latitude finale est obligatoire")
    private Double finalLatitude;
    @NotNull(message = "La longitude finale est obligatoire")
    private Double finalLongitude;
    private String treasureImageUrl;
    private String coordinateFormula;
    private Boolean isActive = true;
}
