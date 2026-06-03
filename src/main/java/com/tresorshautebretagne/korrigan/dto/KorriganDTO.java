package com.tresorshautebretagne.korrigan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KorriganDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
}
