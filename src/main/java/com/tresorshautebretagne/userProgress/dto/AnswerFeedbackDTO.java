package com.tresorshautebretagne.userProgress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerFeedbackDTO {
    private Long questionId;
    private Boolean isCorrect;
    private String explanation;
    private String userAnswer;
}
