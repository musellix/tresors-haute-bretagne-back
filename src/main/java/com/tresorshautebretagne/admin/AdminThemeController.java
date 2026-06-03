package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.ThemeRequest;
import com.tresorshautebretagne.theme.ThemeDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/themes")
@RequiredArgsConstructor
public class AdminThemeController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ThemeDTO> create(@Valid @RequestBody ThemeRequest request) {
        return ResponseEntity.ok(adminService.createTheme(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThemeDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody ThemeRequest request) {
        return ResponseEntity.ok(adminService.updateTheme(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
