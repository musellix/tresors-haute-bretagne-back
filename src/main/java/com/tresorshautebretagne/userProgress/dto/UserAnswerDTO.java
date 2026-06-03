package com.tresorshautebretagne.userProgress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDTO {
    private Long id;
    private Long questionId;
    private String answer;
    private Boolean isCorrect;
}
