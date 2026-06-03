package com.tresorshautebretagne.shared.service;

import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.korrigan.KorriganDTO;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeDTO;
import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.dialogue.Dialogue;
import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserDTO;
import com.tresorshautebretagne.userProgress.UserProgress;
import com.tresorshautebretagne.userProgress.UserProgressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperServiceTest {

    private MapperService mapperService;

    @BeforeEach
    void setUp() {
        mapperService = new MapperService();
    }

    private Korrigan buildKorrigan() {
        Korrigan k = new Korrigan();
        k.setId(1L);
        k.setName("Gribouille");
        k.setDescription("Un korrigan espiègle");
        k.setImageUrl("http://example.com/k.png");
        return k;
    }

    @Test
    void korriganToDTO_mapsAllFields() {
        KorriganDTO dto = mapperService.korriganToDTO(buildKorrigan());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Gribouille");
        assertThat(dto.getDescription()).isEqualTo("Un korrigan espiègle");
        assertThat(dto.getImageUrl()).isEqualTo("http://example.com/k.png");
    }

    @Test
    void themeToDTO_mapsAllFieldsIncludingNestedKorrigan() {
        Theme theme = new Theme();
        theme.setId(2L);
        theme.setName("Forêt enchantée");
        theme.setDescription("La forêt mystérieuse");
        theme.setImageUrl("http://example.com/forest.png");
        theme.setKorrigan(buildKorrigan());

        ThemeDTO dto = mapperService.themeToDTO(theme);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Forêt enchantée");
        assertThat(dto.getDescription()).isEqualTo("La forêt mystérieuse");
        assertThat(dto.getKorriganId()).isEqualTo(1L);
        assertThat(dto.getKorrigan()).isInstanceOf(KorriganDTO.class);
        assertThat(((KorriganDTO) dto.getKorrigan()).getName()).isEqualTo("Gribouille");
    }

    @Test
    void questionToDTO_doesNotExposeCorrectAnswer() {
        Question q = new Question();
        q.setId(5L);
        q.setQuestionOrder(1);
        q.setQuestionText("Combien d'anneaux ?");
        q.setCorrectAnswer("5");
        q.setExplanation("Chaque décennie un anneau");
        q.setQuestionType("SHORT_TEXT");

        QuestionDTO dto = mapperService.questionToDTO(q);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getQuestionOrder()).isEqualTo(1);
        assertThat(dto.getQuestionText()).isEqualTo("Combien d'anneaux ?");
        assertThat(dto.getExplanation()).isEqualTo("Chaque décennie un anneau");
        assertThat(dto.getQuestionType()).isEqualTo("SHORT_TEXT");
        // QuestionDTO n'a pas de champ correctAnswer — le secret reste côté serveur
    }

    @Test
    void dialogueToDTO_mapsAllFields() {
        Dialogue d = new Dialogue();
        d.setId(10L);
        d.setDialogueOrder(2);
        d.setText("Bienvenue, jeune voyageur !");
        d.setAudioUrl("http://example.com/audio.mp3");
        d.setKorrigan(buildKorrigan());

        DialogueDTO dto = mapperService.dialogueToDTO(d);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDialogueOrder()).isEqualTo(2);
        assertThat(dto.getText()).isEqualTo("Bienvenue, jeune voyageur !");
        assertThat(dto.getAudioUrl()).isEqualTo("http://example.com/audio.mp3");
        assertThat(dto.getKorrigan()).isInstanceOf(KorriganDTO.class);
        assertThat(((KorriganDTO) dto.getKorrigan()).getName()).isEqualTo("Gribouille");
    }

    @Test
    void stepToDTO_mapsNestedDialoguesAndQuestions() {
        Step step = new Step();
        step.setId(3L);
        step.setStepOrder(1);
        step.setTitle("Le chêne ancien");
        step.setDescription("Une étape mystérieuse");
        step.setLatitude(48.1);
        step.setLongitude(-1.5);
        step.setRadiusMeters(100);

        Dialogue d = new Dialogue();
        d.setId(10L);
        d.setDialogueOrder(1);
        d.setText("Approche !");
        d.setKorrigan(buildKorrigan());

        Question q = new Question();
        q.setId(20L);
        q.setQuestionOrder(1);
        q.setQuestionText("Quel âge ?");
        q.setCorrectAnswer("200");
        q.setExplanation("Très vieux");
        q.setQuestionType("SHORT_TEXT");

        step.setDialogues(List.of(d));
        step.setQuestions(List.of(q));

        StepDTO dto = mapperService.stepToDTO(step);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getStepOrder()).isEqualTo(1);
        assertThat(dto.getTitle()).isEqualTo("Le chêne ancien");
        assertThat(dto.getLatitude()).isEqualTo(48.1);
        assertThat(dto.getLongitude()).isEqualTo(-1.5);
        assertThat(dto.getRadiusMeters()).isEqualTo(100);
        assertThat(dto.getDialogues()).hasSize(1);
        assertThat(dto.getDialogues().get(0).getText()).isEqualTo("Approche !");
        assertThat(dto.getQuestions()).hasSize(1);
        assertThat(dto.getQuestions().get(0).getQuestionText()).isEqualTo("Quel âge ?");
    }

    @Test
    void stepToDTO_withEmptyCollections() {
        Step step = new Step();
        step.setId(4L);
        step.setStepOrder(2);
        step.setTitle("Étape vide");
        step.setLatitude(48.0);
        step.setLongitude(-1.6);
        step.setRadiusMeters(50);
        step.setDialogues(List.of());
        step.setQuestions(List.of());

        StepDTO dto = mapperService.stepToDTO(step);

        assertThat(dto.getDialogues()).isEmpty();
        assertThat(dto.getQuestions()).isEmpty();
    }

    @Test
    void userProgressToDTO_withoutCompletedAt() {
        User user = new User();
        user.setId(1L);
        TreasureHunt hunt = new TreasureHunt();
        hunt.setId(2L);

        UserProgress progress = new UserProgress();
        progress.setId(1L);
        progress.setUser(user);
        progress.setTreasureHunt(hunt);
        progress.setCurrentStep(2);
        progress.setIsCompleted(false);
        progress.setIsTreasureUnlocked(false);
        progress.setStartedAt(LocalDateTime.of(2024, 6, 1, 10, 0));
        progress.setCompletedAt(null);

        UserProgressDTO dto = mapperService.userProgressToDTO(progress);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getTreasureHuntId()).isEqualTo(2L);
        assertThat(dto.getCurrentStep()).isEqualTo(2);
        assertThat(dto.getIsCompleted()).isFalse();
        assertThat(dto.getIsTreasureUnlocked()).isFalse();
        assertThat(dto.getStartedAt()).isNotNull();
        assertThat(dto.getCompletedAt()).isNull();
    }

    @Test
    void userProgressToDTO_withCompletedAt() {
        User user = new User();
        user.setId(1L);
        TreasureHunt hunt = new TreasureHunt();
        hunt.setId(2L);

        UserProgress progress = new UserProgress();
        progress.setId(1L);
        progress.setUser(user);
        progress.setTreasureHunt(hunt);
        progress.setCurrentStep(3);
        progress.setIsCompleted(true);
        progress.setIsTreasureUnlocked(true);
        progress.setStartedAt(LocalDateTime.of(2024, 6, 1, 10, 0));
        progress.setCompletedAt(LocalDateTime.of(2024, 6, 1, 12, 30));

        UserProgressDTO dto = mapperService.userProgressToDTO(progress);

        assertThat(dto.getIsCompleted()).isTrue();
        assertThat(dto.getIsTreasureUnlocked()).isTrue();
        assertThat(dto.getCompletedAt()).isNotNull();
    }

    @Test
    void userToDTO_mapsFieldsExcludingPassword() {
        User user = new User();
        user.setId(7L);
        user.setEmail("alice@example.com");
        user.setName("Alice");
        user.setPassword("hashed_password");
        user.setAvatarUrl("http://example.com/avatar.png");

        UserDTO dto = mapperService.userToDTO(user);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getAvatarUrl()).isEqualTo("http://example.com/avatar.png");
    }
}
