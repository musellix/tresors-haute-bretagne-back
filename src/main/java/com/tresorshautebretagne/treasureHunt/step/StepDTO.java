package com.tresorshautebretagne.treasureHunt.step;

import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDTO {
    private Long id;
    private Integer stepOrder;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
    private List<DialogueDTO> dialogues;
    private List<QuestionDTO> questions;
}
