package com.tresorshautebretagne.theme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.korrigan.KorriganRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ThemeController.class)
class ThemeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ThemeRepository themeRepository;
    @MockBean KorriganRepository korriganRepository;
    @MockBean MapperService mapperService;

    private ThemeDTO buildDTO(Long id) {
        ThemeDTO dto = new ThemeDTO();
        dto.setId(id);
        dto.setName("Thème " + id);
        dto.setDescription("Description");
        dto.setKorriganId(1L);
        return dto;
    }

    private Theme buildTheme(Long id) {
        Korrigan k = new Korrigan();
        k.setId(1L);
        k.setName("Gribouille");

        Theme t = new Theme();
        t.setId(id);
        t.setName("Thème " + id);
        t.setKorrigan(k);
        return t;
    }

    @Test
    void getAllThemes_returns200WithList() throws Exception {
        Theme theme = buildTheme(1L);
        ThemeDTO dto = buildDTO(1L);

        when(themeRepository.findAll()).thenReturn(List.of(theme));
        when(mapperService.themeToDTO(theme)).thenReturn(dto);

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Thème 1"));
    }

    @Test
    void getAllThemes_returns200WithEmptyList() throws Exception {
        when(themeRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getThemeById_returns200_whenFound() throws Exception {
        Theme theme = buildTheme(1L);
        ThemeDTO dto = buildDTO(1L);

        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(mapperService.themeToDTO(theme)).thenReturn(dto);

        mockMvc.perform(get("/themes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Thème 1"))
                .andExpect(jsonPath("$.korriganId").value(1));
    }

    @Test
    void getThemeById_returns500_whenNotFound() throws Exception {
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/themes/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createTheme_withKorrigan_returns200WithCreatedDTO() throws Exception {
        ThemeDTO requestDTO = buildDTO(null);
        requestDTO.setKorriganId(1L);

        Korrigan korrigan = new Korrigan();
        korrigan.setId(1L);
        Theme saved = buildTheme(3L);
        ThemeDTO responseDTO = buildDTO(3L);

        when(korriganRepository.findById(1L)).thenReturn(Optional.of(korrigan));
        when(themeRepository.save(any(Theme.class))).thenReturn(saved);
        when(mapperService.themeToDTO(saved)).thenReturn(responseDTO);

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void createTheme_withoutKorrigan_returns200() throws Exception {
        ThemeDTO requestDTO = new ThemeDTO();
        requestDTO.setName("Nouveau thème");
        requestDTO.setKorriganId(null);

        Theme saved = buildTheme(4L);
        ThemeDTO responseDTO = buildDTO(4L);

        when(themeRepository.save(any(Theme.class))).thenReturn(saved);
        when(mapperService.themeToDTO(saved)).thenReturn(responseDTO);

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }
}
