package com.tresorshautebretagne.treasureHunt.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private Integer questionOrder;
    private Integer contentOrder;
    private String questionText;
    private String questionType;
    private String explanation;
}
