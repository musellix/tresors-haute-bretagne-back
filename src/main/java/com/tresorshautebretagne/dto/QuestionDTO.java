package com.tresorshautebretagne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private Integer questionOrder;
    private String questionText;
    private String questionType;
    private String explanation;
    // correctAnswer is NOT sent to frontend
}
