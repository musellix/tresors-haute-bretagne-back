package com.tresorshautebretagne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasureHuntDTO {
    private Long id;
    private String title;
    private String description;
    private ThemeDTO theme;
    private Double finalLatitude;
    private Double finalLongitude;
    private String treasureImageUrl;
    private Boolean isActive;
    private List<StepDTO> steps;
}
