package com.tresorshautebretagne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogueDTO {
    private Long id;
    private Integer dialogueOrder;
    private String text;
    private String audioUrl;
    private KorriganDTO korrigan;
}
