package com.tresorshautebretagne.treasureHunt;

import com.tresorshautebretagne.treasureHunt.dto.TreasureHuntDTO;
import com.tresorshautebretagne.treasureHunt.dto.StepDTO;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreasureHuntService {

    private final TreasureHuntRepository treasureHuntRepository;
    private final StepRepository stepRepository;
    private final MapperService mapperService;

    public List<TreasureHuntDTO> getAllActiveTreasureHunts() {
        return treasureHuntRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TreasureHuntDTO getTreasureHuntById(Long id) {
        return treasureHuntRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Treasure hunt not found: " + id));
    }

    public List<TreasureHuntDTO> getTreasureHuntsByTheme(Long themeId) {
        return treasureHuntRepository.findByThemeId(themeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StepDTO getStepById(Long stepId) {
        return stepRepository.findById(stepId)
                .map(mapperService::stepToDTO)
                .orElseThrow(() -> new RuntimeException("Step not found: " + stepId));
    }

    public List<StepDTO> getStepsByTreasureHunt(Long treasureHuntId) {
        return stepRepository.findByTreasureHuntIdOrderByStepOrder(treasureHuntId)
                .stream()
                .map(mapperService::stepToDTO)
                .collect(Collectors.toList());
    }

    private TreasureHuntDTO convertToDTO(TreasureHunt hunt) {
        TreasureHuntDTO dto = new TreasureHuntDTO();
        dto.setId(hunt.getId());
        dto.setTitle(hunt.getTitle());
        dto.setDescription(hunt.getDescription());
        dto.setFinalLatitude(hunt.getFinalLatitude());
        dto.setFinalLongitude(hunt.getFinalLongitude());
        dto.setTreasureImageUrl(hunt.getTreasureImageUrl());
        dto.setIsActive(hunt.getIsActive());
        dto.setTheme(mapperService.themeToDTO(hunt.getTheme()));
        
        List<StepDTO> steps = hunt.getSteps().stream()
                .map(mapperService::stepToDTO)
                .collect(Collectors.toList());
        dto.setSteps(steps);
        
        return dto;
    }
}
