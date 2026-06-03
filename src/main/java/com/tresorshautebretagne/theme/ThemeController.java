package com.tresorshautebretagne.theme;

import com.tresorshautebretagne.shared.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeRepository themeRepository;
    private final MapperService mapperService;

    @GetMapping
    public ResponseEntity<List<ThemeDTO>> getAllThemes() {
        List<ThemeDTO> themes = themeRepository.findAll().stream()
                .map(mapperService::themeToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(themes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThemeDTO> getThemeById(@PathVariable Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found: " + id));
        return ResponseEntity.ok(mapperService.themeToDTO(theme));
    }

}
