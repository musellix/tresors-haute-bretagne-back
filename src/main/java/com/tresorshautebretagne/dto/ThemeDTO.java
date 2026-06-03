package com.tresorshautebretagne.dto;

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
    private KorriganDTO korrigan;
}
