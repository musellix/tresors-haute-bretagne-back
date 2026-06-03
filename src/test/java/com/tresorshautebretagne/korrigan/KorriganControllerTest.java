package com.tresorshautebretagne.korrigan;

import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeDTO;
import com.tresorshautebretagne.theme.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = KorriganController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class KorriganControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean KorriganRepository korriganRepository;
    @MockBean ThemeRepository themeRepository;
    @MockBean MapperService mapperService;

    private KorriganDTO buildDTO(Long id) {
        KorriganDTO dto = new KorriganDTO();
        dto.setId(id);
        dto.setName("Korrigan " + id);
        dto.setDescription("Description");
        dto.setImageUrl("http://example.com/k.png");
        return dto;
    }

    private Korrigan buildEntity(Long id) {
        Korrigan k = new Korrigan();
        k.setId(id);
        k.setName("Korrigan " + id);
        return k;
    }

    @Test
    void getAllKorrigans_returns200WithList() throws Exception {
        Korrigan k = buildEntity(1L);
        KorriganDTO dto = buildDTO(1L);

        when(korriganRepository.findAll()).thenReturn(List.of(k));
        when(mapperService.korriganToDTO(k)).thenReturn(dto);

        mockMvc.perform(get("/korrigans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Korrigan 1"));
    }

    @Test
    void getAllKorrigans_returns200WithEmptyList() throws Exception {
        when(korriganRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/korrigans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getKorriganById_returns200_whenFound() throws Exception {
        Korrigan k = buildEntity(1L);
        KorriganDTO dto = buildDTO(1L);

        when(korriganRepository.findById(1L)).thenReturn(Optional.of(k));
        when(mapperService.korriganToDTO(k)).thenReturn(dto);

        mockMvc.perform(get("/korrigans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Korrigan 1"));
    }

    @Test
    void getKorriganById_returns500_whenNotFound() throws Exception {
        when(korriganRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/korrigans/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getKorriganThemes_returns200WithThemeList() throws Exception {
        Theme theme = new Theme();
        theme.setId(1L);
        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setId(1L);
        themeDTO.setName("Forêt");

        when(themeRepository.findByKorriganId(1L)).thenReturn(List.of(theme));
        when(mapperService.themeToDTO(theme)).thenReturn(themeDTO);

        mockMvc.perform(get("/korrigans/1/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Forêt"));
    }

    @Test
    void getKorriganThemes_returns200WithEmptyList_whenNoThemes() throws Exception {
        when(themeRepository.findByKorriganId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/korrigans/1/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
