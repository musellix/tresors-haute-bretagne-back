package com.tresorshautebretagne.treasureHunt.dialogue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DialogueRepository extends JpaRepository<Dialogue, Long> {
    List<Dialogue> findByStepIdOrderByDialogueOrder(Long stepId);
    List<Dialogue> findByKorriganId(Long korriganId);
}
