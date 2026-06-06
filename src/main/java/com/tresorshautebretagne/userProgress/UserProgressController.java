package com.tresorshautebretagne.userProgress;

import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import com.tresorshautebretagne.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;

    @PostMapping("/{huntId}/start")
    public ResponseEntity<UserProgressDTO> startTreasureHunt(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId) {
        return ResponseEntity.ok(userProgressService.startTreasureHunt(user.getEmail(), huntId));
    }

    @GetMapping
    public ResponseEntity<List<UserProgressDTO>> getUserProgresses(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProgressService.getUserProgresses(user.getEmail()));
    }

    @GetMapping("/{huntId}")
    public ResponseEntity<UserProgressDTO> getUserProgress(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId) {
        return ResponseEntity.ok(userProgressService.getUserProgress(user.getEmail(), huntId));
    }

    @PostMapping("/{huntId}/steps/{stepId}/submit-answers")
    public ResponseEntity<SubmitAnswersResultDTO> submitAnswers(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId,
            @PathVariable Long stepId,
            @RequestBody SubmitAnswersRequest request) {
        return ResponseEntity.ok(userProgressService.submitAnswers(
                user.getEmail(), huntId, stepId, request.getAnswers()));
    }

    @PostMapping("/{huntId}/steps/{stepId}/check-proximity")
    public ResponseEntity<ProximityCheckResult> checkProximity(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId,
            @PathVariable Long stepId,
            @Valid @RequestBody ProximityCheckRequest request) {
        return ResponseEntity.ok(userProgressService.checkProximity(
                user.getEmail(), huntId, stepId,
                request.getLatitude(), request.getLongitude()));
    }

    @GetMapping("/{huntId}/steps/{stepId}/hint")
    public ResponseEntity<HintDTO> getHint(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId,
            @PathVariable Long stepId) {
        return ResponseEntity.ok(userProgressService.getHint(user.getEmail(), huntId, stepId));
    }

    @GetMapping("/{huntId}/treasure-coordinates")
    public ResponseEntity<TreasureCoordinatesDTO> getTreasureCoordinates(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId) {
        CoordinateCalculationService.CalculatedCoordinates coords =
                userProgressService.calculateTreasureCoordinates(user.getEmail(), huntId);
        TreasureCoordinatesDTO dto = new TreasureCoordinatesDTO();
        dto.setLatitude(coords.getLatitude());
        dto.setLongitude(coords.getLongitude());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{huntId}/validate-code")
    public ResponseEntity<Void> validateCode(
            @AuthenticationPrincipal User user,
            @PathVariable Long huntId,
            @RequestBody ValidateCodeRequest request) {
        userProgressService.validateCode(user.getEmail(), huntId, request.getCode());
        return ResponseEntity.ok().build();
    }
}
