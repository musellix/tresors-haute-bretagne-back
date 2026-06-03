package com.tresorshautebretagne.userProgress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {
    private Long id;
    private Long userId;
    private Long treasureHuntId;
    private Integer currentStep;
    private Boolean isCompleted;
    private Boolean isTreasureUnlocked;
    private String startedAt;
    private String completedAt;
}
