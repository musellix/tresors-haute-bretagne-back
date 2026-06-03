package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KorriganRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    private String description;
    private String imageUrl;
}
