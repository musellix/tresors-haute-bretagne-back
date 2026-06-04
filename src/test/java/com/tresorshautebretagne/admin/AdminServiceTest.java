package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.*;
import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.korrigan.KorriganRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeRepository;
import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.TreasureHuntDTO;
import com.tresorshautebretagne.treasureHunt.TreasureHuntRepository;
import com.tresorshautebretagne.treasureHunt.dialogue.Dialogue;
import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.dialogue.DialogueRepository;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.user.Role;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private KorriganRepository korriganRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private TreasureHuntRepository treasureHuntRepository;
    @Mock private StepRepository stepRepository;
    @Mock private DialogueRepository dialogueRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private UserRepository userRepository;
    @Mock private MapperService mapperService;

    @InjectMocks
    private AdminService adminService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Theme buildTheme(Long id) {
        Theme t = new Theme();
        t.setId(id);
        t.setName("Forêt");
        return t;
    }

    private Korrigan buildKorrigan(Long id) {
        Korrigan k = new Korrigan();
        k.setId(id);
        k.setName("Korrig");
        return k;
    }

    private TreasureHunt buildHunt(Long id) {
        TreasureHunt h = new TreasureHunt();
        h.setId(id);
        h.setTitle("Chasse " + id);
        h.setTheme(buildTheme(1L));
        h.setIsActive(true);
        h.setSteps(List.of());
        return h;
    }

    private Step buildStep(Long id, TreasureHunt hunt) {
        Step s = new Step();
        s.setId(id);
        s.setStepOrder(1);
        s.setTitle("Étape");
        s.setTreasureHunt(hunt);
        s.setDialogues(List.of());
        s.setQuestions(List.of());
        return s;
    }

    private TreasureHuntRequest huntRequest() {
        TreasureHuntRequest r = new TreasureHuntRequest();
        r.setTitle("Nouvelle chasse");
        r.setThemeId(1L);
        r.setFinalLatitude(48.0);
        r.setFinalLongitude(-2.0);
        return r;
    }

    // ── getAllTreasureHunts ────────────────────────────────────────────────────

    @Test
    void getAllTreasureHunts_returnsBothActiveAndInactiveHunts() {
        TreasureHunt active = buildHunt(1L);
        TreasureHunt inactive = buildHunt(2L);
        inactive.setIsActive(false);

        when(treasureHuntRepository.findAll()).thenReturn(List.of(active, inactive));
        when(mapperService.themeToDTO(any())).thenReturn(null);

        List<TreasureHuntDTO> result = adminService.getAllTreasureHunts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsActive()).isTrue();
        assertThat(result.get(1).getIsActive()).isFalse();
    }

    // ── createTreasureHunt ────────────────────────────────────────────────────

    @Test
    void createTreasureHunt_savesAndReturnsDTO() {
        TreasureHunt saved = buildHunt(1L);

        when(themeRepository.findById(1L)).thenReturn(Optional.of(buildTheme(1L)));
        when(treasureHuntRepository.save(any())).thenReturn(saved);
        when(mapperService.themeToDTO(any())).thenReturn(null);

        TreasureHuntDTO result = adminService.createTreasureHunt(huntRequest());

        assertThat(result.getId()).isEqualTo(1L);
        verify(treasureHuntRepository).save(any(TreasureHunt.class));
    }

    @Test
    void createTreasureHunt_throws_whenThemeNotFound() {
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());
        TreasureHuntRequest req = huntRequest();
        req.setThemeId(99L);

        assertThatThrownBy(() -> adminService.createTreasureHunt(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Thème introuvable");
    }

    // ── updateTreasureHunt ────────────────────────────────────────────────────

    @Test
    void updateTreasureHunt_updatesFieldsAndReturnsDTO() {
        TreasureHunt existing = buildHunt(1L);
        TreasureHuntRequest req = huntRequest();
        req.setTitle("Titre modifié");

        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(buildTheme(1L)));
        when(treasureHuntRepository.save(any())).thenReturn(existing);
        when(mapperService.themeToDTO(any())).thenReturn(null);

        TreasureHuntDTO result = adminService.updateTreasureHunt(1L, req);

        assertThat(result.getTitle()).isEqualTo("Titre modifié");
    }

    @Test
    void updateTreasureHunt_throws_whenNotFound() {
        when(treasureHuntRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateTreasureHunt(99L, huntRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chasse introuvable");
    }

    // ── toggleActive ──────────────────────────────────────────────────────────

    @Test
    void toggleActive_switchesIsActiveFromTrueToFalse() {
        TreasureHunt hunt = buildHunt(1L);
        hunt.setIsActive(true);

        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(treasureHuntRepository.save(any())).thenReturn(hunt);
        when(mapperService.themeToDTO(any())).thenReturn(null);

        TreasureHuntDTO result = adminService.toggleActive(1L);

        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    void toggleActive_switchesIsActiveFromFalseToTrue() {
        TreasureHunt hunt = buildHunt(1L);
        hunt.setIsActive(false);

        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(treasureHuntRepository.save(any())).thenReturn(hunt);
        when(mapperService.themeToDTO(any())).thenReturn(null);

        TreasureHuntDTO result = adminService.toggleActive(1L);

        assertThat(result.getIsActive()).isTrue();
    }

    // ── deleteTreasureHunt ────────────────────────────────────────────────────

    @Test
    void deleteTreasureHunt_callsRepository() {
        adminService.deleteTreasureHunt(1L);

        verify(treasureHuntRepository).deleteById(1L);
    }

    // ── createStep ────────────────────────────────────────────────────────────

    @Test
    void createStep_savesStepWithCorrectHuntAndReturnsDTO() {
        TreasureHunt hunt = buildHunt(1L);
        Step saved = buildStep(10L, hunt);
        StepDTO dto = new StepDTO();

        StepRequest req = new StepRequest();
        req.setStepOrder(1);
        req.setTitle("Étape 1");
        req.setLatitude(48.0);
        req.setLongitude(-2.0);

        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(stepRepository.save(any())).thenReturn(saved);
        when(mapperService.stepToDTO(saved)).thenReturn(dto);

        assertThat(adminService.createStep(1L, req)).isSameAs(dto);

        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);
        verify(stepRepository).save(captor.capture());
        assertThat(captor.getValue().getTreasureHunt()).isSameAs(hunt);
    }

    @Test
    void createStep_throws_whenHuntNotFound() {
        when(treasureHuntRepository.findById(99L)).thenReturn(Optional.empty());

        StepRequest req = new StepRequest();
        req.setStepOrder(1);
        req.setTitle("X");
        req.setLatitude(0.0);
        req.setLongitude(0.0);

        assertThatThrownBy(() -> adminService.createStep(99L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chasse introuvable");
    }

    // ── createDialogue ────────────────────────────────────────────────────────

    @Test
    void createDialogue_savesAndReturnsDTO() {
        TreasureHunt hunt = buildHunt(1L);
        Step step = buildStep(10L, hunt);
        Korrigan korrigan = buildKorrigan(5L);
        Dialogue saved = new Dialogue();
        DialogueDTO dto = new DialogueDTO();

        DialogueRequest req = new DialogueRequest();
        req.setDialogueOrder(1);
        req.setText("Bonjour !");
        req.setKorriganId(5L);

        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(korriganRepository.findById(5L)).thenReturn(Optional.of(korrigan));
        when(dialogueRepository.save(any())).thenReturn(saved);
        when(mapperService.dialogueToDTO(saved)).thenReturn(dto);

        assertThat(adminService.createDialogue(10L, req)).isSameAs(dto);
    }

    @Test
    void createDialogue_throws_whenKorriganNotFound() {
        TreasureHunt hunt = buildHunt(1L);
        Step step = buildStep(10L, hunt);

        DialogueRequest req = new DialogueRequest();
        req.setDialogueOrder(1);
        req.setText("Texte");
        req.setKorriganId(99L);

        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(korriganRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.createDialogue(10L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Korrigan introuvable");
    }

    // ── createQuestion ────────────────────────────────────────────────────────

    @Test
    void createQuestion_savesAndReturnsDTO() {
        TreasureHunt hunt = buildHunt(1L);
        Step step = buildStep(10L, hunt);
        Question saved = new Question();
        QuestionDTO dto = new QuestionDTO();

        QuestionRequest req = new QuestionRequest();
        req.setQuestionOrder(1);
        req.setQuestionText("Combien ?");
        req.setCorrectAnswer("5");

        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.save(any())).thenReturn(saved);
        when(mapperService.questionToDTO(saved)).thenReturn(dto);

        assertThat(adminService.createQuestion(10L, req)).isSameAs(dto);
    }

    // ── getAllUsers ────────────────────────────────────────────────────────────

    @Test
    void getAllUsers_returnsMappedList() {
        User u1 = new User(); u1.setId(1L);
        User u2 = new User(); u2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(mapperService.userToDTO(any())).thenReturn(new com.tresorshautebretagne.user.UserDTO());

        assertThat(adminService.getAllUsers()).hasSize(2);
    }

    // ── updateRole ────────────────────────────────────────────────────────────

    @Test
    void updateRole_changesRoleAndSaves() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        UpdateRoleRequest req = new UpdateRoleRequest();
        req.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(mapperService.userToDTO(user)).thenReturn(new com.tresorshautebretagne.user.UserDTO());

        adminService.updateRole(1L, req);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(user);
    }

    // ── getDialoguesByStep ────────────────────────────────────────────────────

    @Test
    void getDialoguesByStep_returnsMappedListInOrder() {
        Dialogue d1 = new Dialogue();
        Dialogue d2 = new Dialogue();

        when(dialogueRepository.findByStepIdOrderByDialogueOrder(10L)).thenReturn(List.of(d1, d2));
        when(mapperService.dialogueToDTO(any())).thenReturn(new DialogueDTO());

        assertThat(adminService.getDialoguesByStep(10L)).hasSize(2);
        verify(dialogueRepository).findByStepIdOrderByDialogueOrder(10L);
    }

    @Test
    void getDialoguesByStep_returnsEmptyList_whenNoDialogues() {
        when(dialogueRepository.findByStepIdOrderByDialogueOrder(10L)).thenReturn(List.of());

        assertThat(adminService.getDialoguesByStep(10L)).isEmpty();
    }

    // ── getQuestionsByStep ────────────────────────────────────────────────────

    @Test
    void getQuestionsByStep_returnsMappedListInOrder() {
        Question q1 = new Question();
        Question q2 = new Question();
        Question q3 = new Question();

        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1, q2, q3));
        when(mapperService.questionToDTO(any())).thenReturn(new QuestionDTO());

        assertThat(adminService.getQuestionsByStep(10L)).hasSize(3);
        verify(questionRepository).findByStepIdOrderByQuestionOrder(10L);
    }

    @Test
    void getQuestionsByStep_returnsEmptyList_whenNoQuestions() {
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of());

        assertThat(adminService.getQuestionsByStep(10L)).isEmpty();
    }
}
