package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.UpdateRoleRequest;
import com.tresorshautebretagne.user.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> updateRole(@PathVariable Long id,
                                              @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(adminService.updateRole(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        adminService.toggleUserActive(id);
        return ResponseEntity.ok().build();
    }
}
