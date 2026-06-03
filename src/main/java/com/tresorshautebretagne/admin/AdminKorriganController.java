package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.KorriganRequest;
import com.tresorshautebretagne.korrigan.KorriganDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/korrigans")
@RequiredArgsConstructor
public class AdminKorriganController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<KorriganDTO> create(@Valid @RequestBody KorriganRequest request) {
        return ResponseEntity.ok(adminService.createKorrigan(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KorriganDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody KorriganRequest request) {
        return ResponseEntity.ok(adminService.updateKorrigan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.deleteKorrigan(id);
        return ResponseEntity.noContent().build();
    }
}
