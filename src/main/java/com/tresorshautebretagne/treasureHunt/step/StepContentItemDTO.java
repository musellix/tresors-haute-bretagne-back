package com.tresorshautebretagne.treasureHunt.step;

import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepContentItemDTO {
    private String type; // "dialogue" or "question"
    private Integer contentOrder;
    private DialogueDTO dialogue;
    private QuestionDTO question;

    public static StepContentItemDTO fromDialogue(DialogueDTO dialogue) {
        StepContentItemDTO item = new StepContentItemDTO();
        item.setType("dialogue");
        item.setContentOrder(dialogue.getContentOrder());
        item.setDialogue(dialogue);
        return item;
    }

    public static StepContentItemDTO fromQuestion(QuestionDTO question) {
        StepContentItemDTO item = new StepContentItemDTO();
        item.setType("question");
        item.setContentOrder(question.getContentOrder());
        item.setQuestion(question);
        return item;
    }
}
