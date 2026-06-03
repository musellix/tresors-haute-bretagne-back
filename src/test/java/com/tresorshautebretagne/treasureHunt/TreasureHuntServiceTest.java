package com.tresorshautebretagne.treasureHunt;

import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeDTO;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreasureHuntServiceTest {

    @Mock private TreasureHuntRepository treasureHuntRepository;
    @Mock private StepRepository stepRepository;
    @Mock private MapperService mapperService;

    @InjectMocks
    private TreasureHuntService service;

    private TreasureHunt buildHunt(Long id, boolean active) {
        Korrigan k = new Korrigan();
        k.setId(1L);
        k.setName("Gribouille");

        Theme theme = new Theme();
        theme.setId(1L);
        theme.setName("Forêt");
        theme.setKorrigan(k);

        TreasureHunt hunt = new TreasureHunt();
        hunt.setId(id);
        hunt.setTitle("Chasse " + id);
        hunt.setDescription("Description");
        hunt.setFinalLatitude(47.5);
        hunt.setFinalLongitude(-1.5);
        hunt.setIsActive(active);
        hunt.setTheme(theme);
        hunt.setSteps(List.of());
        return hunt;
    }

    @Test
    void getAllActiveTreasureHunts_returnsMappedList() {
        TreasureHunt hunt = buildHunt(1L, true);
        when(treasureHuntRepository.findByIsActiveTrue()).thenReturn(List.of(hunt));
        when(mapperService.themeToDTO(any())).thenReturn(new ThemeDTO());

        List<TreasureHuntDTO> result = service.getAllActiveTreasureHunts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Chasse 1");
    }

    @Test
    void getAllActiveTreasureHunts_returnsEmptyList_whenNoneActive() {
        when(treasureHuntRepository.findByIsActiveTrue()).thenReturn(List.of());

        List<TreasureHuntDTO> result = service.getAllActiveTreasureHunts();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllActiveTreasureHunts_returnsMultiple() {
        TreasureHunt h1 = buildHunt(1L, true);
        TreasureHunt h2 = buildHunt(2L, true);
        when(treasureHuntRepository.findByIsActiveTrue()).thenReturn(List.of(h1, h2));
        when(mapperService.themeToDTO(any())).thenReturn(new ThemeDTO());

        List<TreasureHuntDTO> result = service.getAllActiveTreasureHunts();

        assertThat(result).hasSize(2);
    }

    @Test
    void getTreasureHuntById_returnsDTO_whenFound() {
        TreasureHunt hunt = buildHunt(1L, true);
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(mapperService.themeToDTO(any())).thenReturn(new ThemeDTO());

        TreasureHuntDTO result = service.getTreasureHuntById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Chasse 1");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void getTreasureHuntById_throws_whenNotFound() {
        when(treasureHuntRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTreasureHuntById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getTreasureHuntsByTheme_returnsMappedList() {
        TreasureHunt hunt = buildHunt(1L, true);
        when(treasureHuntRepository.findByThemeId(2L)).thenReturn(List.of(hunt));
        when(mapperService.themeToDTO(any())).thenReturn(new ThemeDTO());

        List<TreasureHuntDTO> result = service.getTreasureHuntsByTheme(2L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTreasureHuntsByTheme_returnsEmpty_whenNoHuntsForTheme() {
        when(treasureHuntRepository.findByThemeId(99L)).thenReturn(List.of());

        List<TreasureHuntDTO> result = service.getTreasureHuntsByTheme(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getStepById_returnsDTO_whenFound() {
        Step step = new Step();
        step.setId(5L);
        step.setTitle("Étape test");
        step.setDialogues(List.of());
        step.setQuestions(List.of());

        StepDTO stepDTO = new StepDTO();
        stepDTO.setId(5L);
        stepDTO.setTitle("Étape test");

        when(stepRepository.findById(5L)).thenReturn(Optional.of(step));
        when(mapperService.stepToDTO(step)).thenReturn(stepDTO);

        StepDTO result = service.getStepById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getTitle()).isEqualTo("Étape test");
    }

    @Test
    void getStepById_throws_whenNotFound() {
        when(stepRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStepById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getStepsByTreasureHunt_returnsMappedList() {
        Step s1 = new Step();
        s1.setId(10L);
        Step s2 = new Step();
        s2.setId(11L);

        StepDTO dto1 = new StepDTO();
        dto1.setId(10L);
        StepDTO dto2 = new StepDTO();
        dto2.setId(11L);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(s1, s2));
        when(mapperService.stepToDTO(s1)).thenReturn(dto1);
        when(mapperService.stepToDTO(s2)).thenReturn(dto2);

        List<StepDTO> result = service.getStepsByTreasureHunt(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(10L);
        assertThat(result.get(1).getId()).isEqualTo(11L);
    }

    @Test
    void getStepsByTreasureHunt_returnsEmptyList() {
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of());

        List<StepDTO> result = service.getStepsByTreasureHunt(1L);

        assertThat(result).isEmpty();
    }
}
