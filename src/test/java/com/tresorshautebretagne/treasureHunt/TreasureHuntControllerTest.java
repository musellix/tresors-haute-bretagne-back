package com.tresorshautebretagne.treasureHunt;

import com.tresorshautebretagne.config.JwtService;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import com.tresorshautebretagne.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = TreasureHuntController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TreasureHuntControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean TreasureHuntService treasureHuntService;
    @MockBean JwtService jwtService;
    @MockBean UserRepository userRepository;

    private TreasureHuntDTO buildDTO(Long id) {
        TreasureHuntDTO dto = new TreasureHuntDTO();
        dto.setId(id);
        dto.setTitle("Chasse " + id);
        dto.setIsActive(true);
        dto.setFinalLatitude(47.5);
        dto.setFinalLongitude(-1.5);
        return dto;
    }

    @Test
    void getAllActiveTreasureHunts_returns200WithList() throws Exception {
        when(treasureHuntService.getAllActiveTreasureHunts()).thenReturn(List.of(buildDTO(1L), buildDTO(2L)));

        mockMvc.perform(get("/treasure-hunts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getAllActiveTreasureHunts_returns200WithEmptyList() throws Exception {
        when(treasureHuntService.getAllActiveTreasureHunts()).thenReturn(List.of());

        mockMvc.perform(get("/treasure-hunts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTreasureHuntById_returns200_whenFound() throws Exception {
        TreasureHuntDTO dto = buildDTO(1L);
        when(treasureHuntService.getTreasureHuntById(1L)).thenReturn(dto);

        mockMvc.perform(get("/treasure-hunts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Chasse 1"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getTreasureHuntById_returns500_whenNotFound() throws Exception {
        when(treasureHuntService.getTreasureHuntById(99L))
                .thenThrow(new RuntimeException("Treasure hunt not found: 99"));

        mockMvc.perform(get("/treasure-hunts/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getTreasureHuntsByTheme_returns200WithList() throws Exception {
        when(treasureHuntService.getTreasureHuntsByTheme(2L)).thenReturn(List.of(buildDTO(1L)));

        mockMvc.perform(get("/treasure-hunts/theme/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getTreasureHuntsByTheme_returns200WithEmptyList() throws Exception {
        when(treasureHuntService.getTreasureHuntsByTheme(99L)).thenReturn(List.of());

        mockMvc.perform(get("/treasure-hunts/theme/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getStepsByTreasureHunt_returns200WithList() throws Exception {
        StepDTO step = new StepDTO();
        step.setId(5L);
        step.setTitle("Étape 1");
        step.setStepOrder(1);

        when(treasureHuntService.getStepsByTreasureHunt(1L)).thenReturn(List.of(step));

        mockMvc.perform(get("/treasure-hunts/1/steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].title").value("Étape 1"));
    }

    @Test
    void getStepById_returns200_whenFound() throws Exception {
        StepDTO step = new StepDTO();
        step.setId(5L);
        step.setTitle("Étape test");
        step.setLatitude(48.1);
        step.setLongitude(-1.5);

        when(treasureHuntService.getStepById(5L)).thenReturn(step);

        mockMvc.perform(get("/treasure-hunts/steps/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Étape test"));
    }

    @Test
    void getStepById_returns500_whenNotFound() throws Exception {
        when(treasureHuntService.getStepById(99L))
                .thenThrow(new RuntimeException("Step not found: 99"));

        mockMvc.perform(get("/treasure-hunts/steps/99"))
                .andExpect(status().is5xxServerError());
    }
}
