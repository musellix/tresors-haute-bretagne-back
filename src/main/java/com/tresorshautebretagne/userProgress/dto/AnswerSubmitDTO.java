package com.tresorshautebretagne.userProgress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmitDTO {
    private Long questionId;
    private String answer;
}
