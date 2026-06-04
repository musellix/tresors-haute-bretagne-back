package com.tresorshautebretagne.userProgress;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswersRequest {
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
    }
}
