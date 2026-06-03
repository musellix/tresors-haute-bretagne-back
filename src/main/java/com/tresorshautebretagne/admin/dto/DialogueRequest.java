package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DialogueRequest {
    @NotNull(message = "L'ordre du dialogue est obligatoire")
    private Integer dialogueOrder;
    @NotBlank(message = "Le texte est obligatoire")
    private String text;
    @NotNull(message = "Le korrigan est obligatoire")
    private Long korriganId;
    private String audioUrl;
}
