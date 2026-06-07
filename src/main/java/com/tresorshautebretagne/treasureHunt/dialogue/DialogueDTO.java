package com.tresorshautebretagne.treasureHunt.dialogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogueDTO {
    private Long id;
    private Integer dialogueOrder;
    private Integer contentOrder;
    private String text;
    private String audioUrl;
    private Object korrigan;
}
