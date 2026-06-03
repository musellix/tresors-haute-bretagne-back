package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemeRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    private String description;
    private String imageUrl;
    @NotNull(message = "Le korrigan est obligatoire")
    private Long korriganId;
}
