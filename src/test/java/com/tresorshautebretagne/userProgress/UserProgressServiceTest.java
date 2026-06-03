package com.tresorshautebretagne.userProgress;

import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.TreasureHuntRepository;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserRepository;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswer;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProgressServiceTest {

    @Mock private UserProgressRepository userProgressRepository;
    @Mock private UserAnswerRepository userAnswerRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private StepRepository stepRepository;
    @Mock private TreasureHuntRepository treasureHuntRepository;
    @Mock private UserRepository userRepository;
    @Mock private MapperService mapperService;
    @Mock private CoordinateCalculationService coordinateService;

    @InjectMocks
    private UserProgressService service;

    private User buildUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setEmail("user" + id + "@test.com");
        u.setName("User " + id);
        return u;
    }

    private TreasureHunt buildHunt(Long id) {
        TreasureHunt h = new TreasureHunt();
        h.setId(id);
        h.setTitle("Chasse " + id);
        return h;
    }

    private UserProgress buildProgress(Long userId, Long huntId) {
        UserProgress p = new UserProgress();
        p.setId(1L);
        p.setUser(buildUser(userId));
        p.setTreasureHunt(buildHunt(huntId));
        p.setCurrentStep(1);
        p.setIsCompleted(false);
        p.setIsTreasureUnlocked(false);
        p.setStartedAt(LocalDateTime.now());
        return p;
    }

    @Test
    void startTreasureHunt_createsNewProgress_whenNoneExists() {
        User user = buildUser(1L);
        TreasureHunt hunt = buildHunt(1L);
        UserProgress saved = buildProgress(1L, 1L);
        UserProgressDTO dto = new UserProgressDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.empty());
        when(userProgressRepository.save(any())).thenReturn(saved);
        when(mapperService.userProgressToDTO(saved)).thenReturn(dto);

        UserProgressDTO result = service.startTreasureHunt(1L, 1L);

        assertThat(result).isSameAs(dto);
        verify(userProgressRepository).save(any(UserProgress.class));
    }

    @Test
    void startTreasureHunt_resetsExistingProgress() {
        User user = buildUser(1L);
        TreasureHunt hunt = buildHunt(1L);
        UserProgress existing = buildProgress(1L, 1L);
        existing.setCurrentStep(3);
        existing.setIsCompleted(true);
        existing.setIsTreasureUnlocked(true);
        UserProgressDTO dto = new UserProgressDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(existing));
        when(userProgressRepository.save(any())).thenReturn(existing);
        when(mapperService.userProgressToDTO(existing)).thenReturn(dto);

        service.startTreasureHunt(1L, 1L);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentStep()).isEqualTo(1);
        assertThat(captor.getValue().getIsCompleted()).isFalse();
        assertThat(captor.getValue().getIsTreasureUnlocked()).isFalse();
    }

    @Test
    void startTreasureHunt_throws_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startTreasureHunt(99L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void startTreasureHunt_throws_whenHuntNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(treasureHuntRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startTreasureHunt(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Treasure hunt not found");
    }

    @Test
    void submitAnswer_savesCorrectAnswer() {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("5");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        service.submitAnswer(1L, 1L, "5");

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        verify(userAnswerRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCorrect()).isTrue();
        assertThat(captor.getValue().getAnswer()).isEqualTo("5");
    }

    @Test
    void submitAnswer_savesIncorrectAnswer() {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("5");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        service.submitAnswer(1L, 1L, "3");

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        verify(userAnswerRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCorrect()).isFalse();
    }

    @Test
    void submitAnswer_isCaseAndSpaceInsensitive() {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("  Forêt  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        service.submitAnswer(1L, 1L, "forêt");

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        verify(userAnswerRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCorrect()).isTrue();
    }

    @Test
    void getUserProgress_returnsDTO_whenFound() {
        UserProgress progress = buildProgress(1L, 1L);
        UserProgressDTO dto = new UserProgressDTO();

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(mapperService.userProgressToDTO(progress)).thenReturn(dto);

        UserProgressDTO result = service.getUserProgress(1L, 1L);

        assertThat(result).isSameAs(dto);
    }

    @Test
    void getUserProgress_throws_whenNotFound() {
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserProgress(1L, 99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getUserProgresses_returnsMappedList() {
        UserProgress p1 = buildProgress(1L, 1L);
        UserProgress p2 = buildProgress(1L, 2L);
        UserProgressDTO dto1 = new UserProgressDTO();
        UserProgressDTO dto2 = new UserProgressDTO();

        when(userProgressRepository.findByUserId(1L)).thenReturn(List.of(p1, p2));
        when(mapperService.userProgressToDTO(p1)).thenReturn(dto1);
        when(mapperService.userProgressToDTO(p2)).thenReturn(dto2);

        List<UserProgressDTO> result = service.getUserProgresses(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void checkAndUnlockTreasure_unlocksWhenAllQuestionsCorrect() {
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L, 1L);
        progress.setTreasureHunt(hunt);

        Step step = new Step();
        step.setId(10L);
        Question question = new Question();
        question.setId(20L);
        UserAnswer correct = new UserAnswer();
        correct.setIsCorrect(true);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 20L)).thenReturn(List.of(correct));

        service.checkAndUnlockTreasure(1L, 1L);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCompleted()).isTrue();
        assertThat(captor.getValue().getIsTreasureUnlocked()).isTrue();
        assertThat(captor.getValue().getCompletedAt()).isNotNull();
    }

    @Test
    void checkAndUnlockTreasure_doesNotUnlockWhenAnswerIncorrect() {
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L, 1L);
        progress.setTreasureHunt(hunt);

        Step step = new Step();
        step.setId(10L);
        Question question = new Question();
        question.setId(20L);
        UserAnswer wrong = new UserAnswer();
        wrong.setIsCorrect(false);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 20L)).thenReturn(List.of(wrong));

        service.checkAndUnlockTreasure(1L, 1L);

        verify(userProgressRepository, never()).save(any());
    }

    @Test
    void allQuestionsAnsweredCorrectly_returnsTrueWhenNoQuestions() {
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of());

        assertThat(service.allQuestionsAnsweredCorrectly(1L, 10L)).isTrue();
    }

    @Test
    void allQuestionsAnsweredCorrectly_returnsTrueWhenAllCorrect() {
        Question q = new Question();
        q.setId(20L);
        UserAnswer correct = new UserAnswer();
        correct.setIsCorrect(true);

        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 20L)).thenReturn(List.of(correct));

        assertThat(service.allQuestionsAnsweredCorrectly(1L, 10L)).isTrue();
    }

    @Test
    void allQuestionsAnsweredCorrectly_returnsFalseWhenNoAnswer() {
        Question q = new Question();
        q.setId(20L);

        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 20L)).thenReturn(List.of());

        assertThat(service.allQuestionsAnsweredCorrectly(1L, 10L)).isFalse();
    }

    @Test
    void allQuestionsAnsweredCorrectly_returnsFalseWhenAnswerWrong() {
        Question q = new Question();
        q.setId(20L);
        UserAnswer wrong = new UserAnswer();
        wrong.setIsCorrect(false);

        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 20L)).thenReturn(List.of(wrong));

        assertThat(service.allQuestionsAnsweredCorrectly(1L, 10L)).isFalse();
    }

    @Test
    void calculateTreasureCoordinates_returnsCoords_whenUnlocked() {
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L, 1L);
        progress.setTreasureHunt(hunt);
        progress.setIsTreasureUnlocked(true);

        CoordinateCalculationService.CalculatedCoordinates coords =
                new CoordinateCalculationService.CalculatedCoordinates(47.5, -1.5);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(coordinateService.calculateCoordinates(1L, hunt)).thenReturn(coords);

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateTreasureCoordinates(1L, 1L);

        assertThat(result.getLatitude()).isEqualTo(47.5);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }

    @Test
    void calculateTreasureCoordinates_throws_whenTreasureLocked() {
        UserProgress progress = buildProgress(1L, 1L);
        progress.setIsTreasureUnlocked(false);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));

        assertThatThrownBy(() -> service.calculateTreasureCoordinates(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not yet unlocked");
    }

    @Test
    void advanceStep_incrementsCurrentStep() {
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L, 1L);
        progress.setCurrentStep(1);
        progress.setTreasureHunt(hunt);

        Step s1 = new Step(); s1.setId(1L);
        Step s2 = new Step(); s2.setId(2L);
        Step s3 = new Step(); s3.setId(3L);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(s1, s2, s3));

        service.advanceStep(1L, 1L);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentStep()).isEqualTo(2);
    }

    @Test
    void advanceStep_doesNotAdvanceBeyondLastStep() {
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L, 1L);
        progress.setCurrentStep(3);
        progress.setTreasureHunt(hunt);

        Step s1 = new Step(); s1.setId(1L);
        Step s2 = new Step(); s2.setId(2L);
        Step s3 = new Step(); s3.setId(3L);

        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(s1, s2, s3));

        service.advanceStep(1L, 1L);

        verify(userProgressRepository, never()).save(any());
    }
}
