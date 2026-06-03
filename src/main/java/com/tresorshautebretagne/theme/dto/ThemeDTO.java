package com.tresorshautebretagne.theme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long korriganId;
    private Object korrigan; // KorriganDTO
}
