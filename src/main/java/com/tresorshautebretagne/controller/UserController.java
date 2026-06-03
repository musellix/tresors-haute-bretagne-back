package com.tresorshautebretagne.controller;

import com.tresorshautebretagne.dto.UserDTO;
import com.tresorshautebretagne.entity.User;
import com.tresorshautebretagne.repository.UserRepository;
import com.tresorshautebretagne.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MapperService mapperService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return ResponseEntity.ok(mapperService.userToDTO(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        // TODO: Add password hashing
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        // Password handling should be added
        
        User saved = userRepository.save(user);
        return ResponseEntity.ok(mapperService.userToDTO(saved));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return ResponseEntity.ok(mapperService.userToDTO(user));
    }
}
