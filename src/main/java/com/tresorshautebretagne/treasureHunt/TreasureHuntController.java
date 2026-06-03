package com.tresorshautebretagne.treasureHunt;

import com.tresorshautebretagne.treasureHunt.TreasureHuntDTO;
import com.tresorshautebretagne.treasureHunt.dto.StepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treasure-hunts")
@RequiredArgsConstructor
public class TreasureHuntController {

    private final TreasureHuntService treasureHuntService;

    @GetMapping
    public ResponseEntity<List<TreasureHuntDTO>> getAllActiveTreasureHunts() {
        return ResponseEntity.ok(treasureHuntService.getAllActiveTreasureHunts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreasureHuntDTO> getTreasureHuntById(@PathVariable Long id) {
        return ResponseEntity.ok(treasureHuntService.getTreasureHuntById(id));
    }

    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<TreasureHuntDTO>> getTreasureHuntsByTheme(@PathVariable Long themeId) {
        return ResponseEntity.ok(treasureHuntService.getTreasureHuntsByTheme(themeId));
    }

    @GetMapping("/{huntId}/steps")
    public ResponseEntity<List<StepDTO>> getStepsByTreasureHunt(@PathVariable Long huntId) {
        return ResponseEntity.ok(treasureHuntService.getStepsByTreasureHunt(huntId));
    }

    @GetMapping("/steps/{stepId}")
    public ResponseEntity<StepDTO> getStepById(@PathVariable Long stepId) {
        return ResponseEntity.ok(treasureHuntService.getStepById(stepId));
    }
}
