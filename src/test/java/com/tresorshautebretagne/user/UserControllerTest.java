package com.tresorshautebretagne.user;

import com.tresorshautebretagne.config.JwtService;
import com.tresorshautebretagne.shared.service.MapperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UserRepository userRepository;
    @MockBean MapperService mapperService;
    @MockBean JwtService jwtService;

    private User buildUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setEmail("user" + id + "@example.com");
        u.setName("User " + id);
        u.setPassword("hashed");
        return u;
    }

    private UserDTO buildDTO(Long id) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setEmail("user" + id + "@example.com");
        dto.setName("User " + id);
        return dto;
    }

    @Test
    void getUserById_returns200_whenFound() throws Exception {
        User user = buildUser(1L);
        UserDTO dto = buildDTO(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapperService.userToDTO(user)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.name").value("User 1"));
    }

    @Test
    void getUserById_returns500_whenNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getUserByEmail_returns200_whenFound() throws Exception {
        User user = buildUser(1L);
        UserDTO dto = buildDTO(1L);

        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        when(mapperService.userToDTO(user)).thenReturn(dto);

        mockMvc.perform(get("/users/email/user1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void getUserByEmail_returns500_whenNotFound() throws Exception {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email/nobody@example.com"))
                .andExpect(status().is5xxServerError());
    }
}
