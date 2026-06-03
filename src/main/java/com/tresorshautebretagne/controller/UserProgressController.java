package com.tresorshautebretagne.controller;

import com.tresorshautebretagne.dto.*;
import com.tresorshautebretagne.entity.Question;
import com.tresorshautebretagne.repository.QuestionRepository;
import com.tresorshautebretagne.service.UserProgressService;
import com.tresorshautebretagne.service.CoordinateCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;
    private final QuestionRepository questionRepository;

    @PostMapping("/start/{userId}/{treasureHuntId}")
    public ResponseEntity<UserProgressDTO> startTreasureHunt(
            @PathVariable Long userId,
            @PathVariable Long treasureHuntId) {
        UserProgressDTO progress = userProgressService.startTreasureHunt(userId, treasureHuntId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/{userId}/{treasureHuntId}")
    public ResponseEntity<UserProgressDTO> getUserProgress(
            @PathVariable Long userId,
            @PathVariable Long treasureHuntId) {
        UserProgressDTO progress = userProgressService.getUserProgress(userId, treasureHuntId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserProgressDTO>> getUserProgresses(
            @PathVariable Long userId) {
        List<UserProgressDTO> progresses = userProgressService.getUserProgresses(userId);
        return ResponseEntity.ok(progresses);
    }

    @PostMapping("/{userId}/answer")
    public ResponseEntity<AnswerFeedbackDTO> submitAnswer(
            @PathVariable Long userId,
            @RequestBody AnswerSubmitDTO answerSubmit) {
        
        Question question = questionRepository.findById(answerSubmit.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Submit answer
        userProgressService.submitAnswer(userId, answerSubmit.getQuestionId(), answerSubmit.getAnswer());

        // Check if correct
        String normalizedAnswer = answerSubmit.getAnswer().trim().toLowerCase();
        String normalizedCorrect = question.getCorrectAnswer().trim().toLowerCase();
        Boolean isCorrect = normalizedAnswer.equals(normalizedCorrect);

        // Build feedback
        AnswerFeedbackDTO feedback = new AnswerFeedbackDTO();
        feedback.setQuestionId(question.getId());
        feedback.setIsCorrect(isCorrect);
        feedback.setExplanation(question.getExplanation());
        feedback.setUserAnswer(answerSubmit.getAnswer());

        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/{userId}/{treasureHuntId}/check-unlock")
    public ResponseEntity<Void> checkAndUnlockTreasure(
            @PathVariable Long userId,
            @PathVariable Long treasureHuntId) {
        userProgressService.checkAndUnlockTreasure(userId, treasureHuntId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{treasureHuntId}/treasure-coordinates")
    public ResponseEntity<TreasureCoordinatesDTO> getTreasureCoordinates(
            @PathVariable Long userId,
            @PathVariable Long treasureHuntId) {
        CoordinateCalculationService.CalculatedCoordinates coords = 
            userProgressService.calculateTreasureCoordinates(userId, treasureHuntId);
        
        TreasureCoordinatesDTO dto = new TreasureCoordinatesDTO();
        dto.setLatitude(coords.getLatitude());
        dto.setLongitude(coords.getLongitude());
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{userId}/{treasureHuntId}/advance-step")
    public ResponseEntity<UserProgressDTO> advanceStep(
            @PathVariable Long userId,
            @PathVariable Long treasureHuntId) {
        userProgressService.advanceStep(userId, treasureHuntId);
        UserProgressDTO progress = userProgressService.getUserProgress(userId, treasureHuntId);
        return ResponseEntity.ok(progress);
    }
}
