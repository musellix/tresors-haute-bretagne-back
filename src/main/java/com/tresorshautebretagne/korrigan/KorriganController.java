package com.tresorshautebretagne.korrigan;

import com.tresorshautebretagne.theme.ThemeDTO;
import com.tresorshautebretagne.theme.ThemeRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/korrigans")
@RequiredArgsConstructor
public class KorriganController {

    private final KorriganRepository korriganRepository;
    private final ThemeRepository themeRepository;
    private final MapperService mapperService;

    @GetMapping
    public ResponseEntity<List<KorriganDTO>> getAllKorrigans() {
        List<KorriganDTO> korrigans = korriganRepository.findAll().stream()
                .map(mapperService::korriganToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(korrigans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KorriganDTO> getKorriganById(@PathVariable Long id) {
        Korrigan korrigan = korriganRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korrigan not found: " + id));
        return ResponseEntity.ok(mapperService.korriganToDTO(korrigan));
    }

    @GetMapping("/{id}/themes")
    public ResponseEntity<List<ThemeDTO>> getKorriganThemes(@PathVariable Long id) {
        List<ThemeDTO> themes = themeRepository.findByKorriganId(id).stream()
                .map(mapperService::themeToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(themes);
    }

}
