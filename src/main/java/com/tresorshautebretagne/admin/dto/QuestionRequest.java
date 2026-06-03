package com.tresorshautebretagne.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotNull(message = "L'ordre de la question est obligatoire")
    private Integer questionOrder;
    @NotBlank(message = "Le texte de la question est obligatoire")
    private String questionText;
    @NotBlank(message = "La réponse correcte est obligatoire")
    private String correctAnswer;
    private String explanation;
    private String questionType = "SHORT_TEXT";
}
