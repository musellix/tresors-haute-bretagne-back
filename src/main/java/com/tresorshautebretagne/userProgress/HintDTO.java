package com.tresorshautebretagne.userProgress;

import lombok.Data;

import java.util.List;

@Data
public class HintDTO {
    private List<Long> wrongQuestionIds;
}
