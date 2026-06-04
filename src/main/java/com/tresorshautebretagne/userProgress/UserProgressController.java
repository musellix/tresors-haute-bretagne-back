package com.tresorshautebretagne.userProgress;

import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;

    @PostMapping("/{huntId}/start")
    public ResponseEntity<UserProgressDTO> startTreasureHunt(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId) {
        return ResponseEntity.ok(userProgressService.startTreasureHunt(principal.getUsername(), huntId));
    }

    @GetMapping
    public ResponseEntity<List<UserProgressDTO>> getUserProgresses(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(userProgressService.getUserProgresses(principal.getUsername()));
    }

    @GetMapping("/{huntId}")
    public ResponseEntity<UserProgressDTO> getUserProgress(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId) {
        return ResponseEntity.ok(userProgressService.getUserProgress(principal.getUsername(), huntId));
    }

    @PostMapping("/{huntId}/steps/{stepId}/submit-answers")
    public ResponseEntity<SubmitAnswersResultDTO> submitAnswers(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId,
            @PathVariable Long stepId,
            @RequestBody SubmitAnswersRequest request) {
        return ResponseEntity.ok(userProgressService.submitAnswers(
                principal.getUsername(), huntId, stepId, request.getAnswers()));
    }

    @PostMapping("/{huntId}/steps/{stepId}/check-proximity")
    public ResponseEntity<ProximityCheckResult> checkProximity(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId,
            @PathVariable Long stepId,
            @Valid @RequestBody ProximityCheckRequest request) {
        return ResponseEntity.ok(userProgressService.checkProximity(
                principal.getUsername(), huntId, stepId,
                request.getLatitude(), request.getLongitude()));
    }

    @GetMapping("/{huntId}/steps/{stepId}/hint")
    public ResponseEntity<HintDTO> getHint(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId,
            @PathVariable Long stepId) {
        return ResponseEntity.ok(userProgressService.getHint(principal.getUsername(), huntId, stepId));
    }

    @GetMapping("/{huntId}/treasure-coordinates")
    public ResponseEntity<TreasureCoordinatesDTO> getTreasureCoordinates(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId) {
        CoordinateCalculationService.CalculatedCoordinates coords =
                userProgressService.calculateTreasureCoordinates(principal.getUsername(), huntId);
        TreasureCoordinatesDTO dto = new TreasureCoordinatesDTO();
        dto.setLatitude(coords.getLatitude());
        dto.setLongitude(coords.getLongitude());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{huntId}/validate-code")
    public ResponseEntity<Void> validateCode(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long huntId,
            @RequestBody ValidateCodeRequest request) {
        userProgressService.validateCode(principal.getUsername(), huntId, request.getCode());
        return ResponseEntity.ok().build();
    }
}
