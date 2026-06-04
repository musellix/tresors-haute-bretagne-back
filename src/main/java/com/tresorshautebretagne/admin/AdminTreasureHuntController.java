package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.*;
import com.tresorshautebretagne.treasureHunt.TreasureHuntDTO;
import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminTreasureHuntController {

    private final AdminService adminService;

    // ── Chasses ──────────────────────────────────────────────────────────────

    @GetMapping("/treasure-hunts")
    public ResponseEntity<List<TreasureHuntDTO>> getAllHunts() {
        return ResponseEntity.ok(adminService.getAllTreasureHunts());
    }

    @PostMapping("/treasure-hunts")
    public ResponseEntity<TreasureHuntDTO> createHunt(@Valid @RequestBody TreasureHuntRequest request) {
        return ResponseEntity.ok(adminService.createTreasureHunt(request));
    }

    @PutMapping("/treasure-hunts/{id}")
    public ResponseEntity<TreasureHuntDTO> updateHunt(@PathVariable Long id,
                                                      @Valid @RequestBody TreasureHuntRequest request) {
        return ResponseEntity.ok(adminService.updateTreasureHunt(id, request));
    }

    @PatchMapping("/treasure-hunts/{id}/toggle-active")
    public ResponseEntity<TreasureHuntDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleActive(id));
    }

    @DeleteMapping("/treasure-hunts/{id}")
    public ResponseEntity<Void> deleteHunt(@PathVariable Long id) {
        adminService.deleteTreasureHunt(id);
        return ResponseEntity.noContent().build();
    }

    // ── Étapes ───────────────────────────────────────────────────────────────

    @PostMapping("/treasure-hunts/{huntId}/steps")
    public ResponseEntity<StepDTO> createStep(@PathVariable Long huntId,
                                              @Valid @RequestBody StepRequest request) {
        return ResponseEntity.ok(adminService.createStep(huntId, request));
    }

    @PutMapping("/steps/{stepId}")
    public ResponseEntity<StepDTO> updateStep(@PathVariable Long stepId,
                                              @Valid @RequestBody StepRequest request) {
        return ResponseEntity.ok(adminService.updateStep(stepId, request));
    }

    @DeleteMapping("/steps/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        adminService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }

    // ── Dialogues ────────────────────────────────────────────────────────────

    @GetMapping("/steps/{stepId}/dialogues")
    public ResponseEntity<List<DialogueDTO>> getDialogues(@PathVariable Long stepId) {
        return ResponseEntity.ok(adminService.getDialoguesByStep(stepId));
    }

    @PostMapping("/steps/{stepId}/dialogues")
    public ResponseEntity<DialogueDTO> createDialogue(@PathVariable Long stepId,
                                                      @Valid @RequestBody DialogueRequest request) {
        return ResponseEntity.ok(adminService.createDialogue(stepId, request));
    }

    @PutMapping("/dialogues/{dialogueId}")
    public ResponseEntity<DialogueDTO> updateDialogue(@PathVariable Long dialogueId,
                                                      @Valid @RequestBody DialogueRequest request) {
        return ResponseEntity.ok(adminService.updateDialogue(dialogueId, request));
    }

    @DeleteMapping("/dialogues/{dialogueId}")
    public ResponseEntity<Void> deleteDialogue(@PathVariable Long dialogueId) {
        adminService.deleteDialogue(dialogueId);
        return ResponseEntity.noContent().build();
    }

    // ── Questions ────────────────────────────────────────────────────────────

    @GetMapping("/steps/{stepId}/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions(@PathVariable Long stepId) {
        return ResponseEntity.ok(adminService.getQuestionsByStep(stepId));
    }

    @PostMapping("/steps/{stepId}/questions")
    public ResponseEntity<QuestionDTO> createQuestion(@PathVariable Long stepId,
                                                      @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(adminService.createQuestion(stepId, request));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long questionId,
                                                      @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(adminService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        adminService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
