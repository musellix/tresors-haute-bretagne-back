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

    private static final String EMAIL = "user@test.com";

    private User buildUser() {
        User u = new User();
        u.setId(1L);
        u.setEmail(EMAIL);
        u.setName("User 1");
        return u;
    }

    private TreasureHunt buildHunt(Long id) {
        TreasureHunt h = new TreasureHunt();
        h.setId(id);
        h.setTitle("Chasse " + id);
        h.setAccessCode("ABCD1234");
        return h;
    }

    private UserProgress buildProgress(Long huntId) {
        UserProgress p = new UserProgress();
        p.setId(1L);
        p.setUser(buildUser());
        p.setTreasureHunt(buildHunt(huntId));
        p.setCurrentStep(1);
        p.setIsCompleted(false);
        p.setIsTreasureUnlocked(false);
        p.setStartedAt(LocalDateTime.now());
        return p;
    }

    // ── startTreasureHunt ───────────────────────────────────────────────────

    @Test
    void startTreasureHunt_createsNewProgress_whenNoneExists() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        UserProgress saved = buildProgress(1L);
        UserProgressDTO dto = new UserProgressDTO();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.empty());
        when(userProgressRepository.save(any())).thenReturn(saved);
        when(mapperService.userProgressToDTO(saved)).thenReturn(dto);

        assertThat(service.startTreasureHunt(EMAIL, 1L)).isSameAs(dto);
        verify(userProgressRepository).save(any(UserProgress.class));
    }

    @Test
    void startTreasureHunt_resetsExistingProgress() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        UserProgress existing = buildProgress(1L);
        existing.setCurrentStep(3);
        existing.setIsCompleted(true);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(existing));
        when(userProgressRepository.save(any())).thenReturn(existing);
        when(mapperService.userProgressToDTO(any())).thenReturn(new UserProgressDTO());

        service.startTreasureHunt(EMAIL, 1L);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentStep()).isEqualTo(1);
        assertThat(captor.getValue().getIsCompleted()).isFalse();
    }

    @Test
    void startTreasureHunt_throws_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startTreasureHunt(EMAIL, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ── submitAnswers ────────────────────────────────────────────────────────

    private Step buildStep(Long id, Long huntId, int order) {
        Step s = new Step();
        s.setId(id);
        s.setStepOrder(order);
        s.setTreasureHunt(buildHunt(huntId));
        return s;
    }

    private Question buildQuestion(Long id, String correctAnswer) {
        Question q = new Question();
        q.setId(id);
        q.setCorrectAnswer(correctAnswer);
        return q;
    }

    @Test
    void submitAnswers_savesCorrectAnswerAsUpsert_andAdvancesStep() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        Step step = buildStep(10L, 1L, 1);
        Question question = buildQuestion(20L, "5");
        UserProgress progress = buildProgress(1L);

        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(20L);
        item.setAnswer("5");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L)).thenReturn(Optional.empty());
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        // allQuestionsAnsweredCorrectly check
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L)).thenReturn(Optional.of(correctAnswer(question)));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        // two steps → not last
        Step step2 = buildStep(11L, 1L, 2);
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step, step2));

        SubmitAnswersResultDTO result = service.submitAnswers(EMAIL, 1L, 10L, List.of(item));

        assertThat(result.getAllCorrect()).isTrue();
        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentStep()).isEqualTo(2);
    }

    @Test
    void submitAnswers_updatesExistingAnswer_onRetry() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question question = buildQuestion(20L, "5");
        UserAnswer existing = new UserAnswer();
        existing.setId(99L);
        existing.setIsCorrect(false);
        existing.setAnswer("3");

        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(20L);
        item.setAnswer("5");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(buildHunt(1L)));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L))
                .thenReturn(Optional.of(existing));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L))
                .thenReturn(Optional.of(buildProgress(1L)));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L))
                .thenReturn(List.of(step));

        service.submitAnswers(EMAIL, 1L, 10L, List.of(item));

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        verify(userAnswerRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(99L);
        assertThat(captor.getValue().getIsCorrect()).isTrue();
        assertThat(captor.getValue().getAnswer()).isEqualTo("5");
    }

    @Test
    void submitAnswers_unlocksTreasure_whenLastStepAllCorrect() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question question = buildQuestion(20L, "5");
        UserProgress progress = buildProgress(1L);

        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(20L);
        item.setAnswer("5");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(buildHunt(1L)));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L))
                .thenReturn(Optional.of(correctAnswer(question)));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        // single step → last step
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));

        SubmitAnswersResultDTO result = service.submitAnswers(EMAIL, 1L, 10L, List.of(item));

        assertThat(result.getAllCorrect()).isTrue();
        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsTreasureUnlocked()).isTrue();
    }

    @Test
    void submitAnswers_returnsFalse_whenAnswerWrong() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question question = buildQuestion(20L, "5");

        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(20L);
        item.setAnswer("3");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(buildHunt(1L)));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L)).thenReturn(Optional.empty());
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));

        SubmitAnswersResultDTO result = service.submitAnswers(EMAIL, 1L, 10L, List.of(item));

        assertThat(result.getAllCorrect()).isFalse();
        verify(userProgressRepository, never()).save(any());
    }

    @Test
    void submitAnswers_isCaseAndSpaceInsensitive() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question question = buildQuestion(20L, "  Forêt  ");

        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(20L);
        item.setAnswer("forêt");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(buildHunt(1L)));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findById(20L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L))
                .thenReturn(Optional.of(correctAnswer(question)));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(question));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L))
                .thenReturn(Optional.of(buildProgress(1L)));
        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        service.submitAnswers(EMAIL, 1L, 10L, List.of(item));
        verify(userAnswerRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCorrect()).isTrue();
    }

    @Test
    void submitAnswers_throws_whenStepDoesNotBelongToHunt() {
        User user = buildUser();
        Step step = buildStep(10L, 99L, 1); // belongs to hunt 99, not 1

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(buildHunt(1L)));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));

        assertThatThrownBy(() -> service.submitAnswers(EMAIL, 1L, 10L, List.of()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not belong");
    }

    // ── getHint ──────────────────────────────────────────────────────────────

    @Test
    void getHint_returnsWrongQuestionIds() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question q1 = buildQuestion(20L, "5");
        Question q2 = buildQuestion(21L, "Paris");

        UserAnswer wrongAnswer = new UserAnswer();
        wrongAnswer.setIsCorrect(false);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1, q2));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L))
                .thenReturn(Optional.of(wrongAnswer));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 21L))
                .thenReturn(Optional.empty());

        HintDTO hint = service.getHint(EMAIL, 1L, 10L);

        assertThat(hint.getWrongQuestionIds()).containsExactlyInAnyOrder(20L, 21L);
    }

    @Test
    void getHint_excludesCorrectQuestions() {
        User user = buildUser();
        Step step = buildStep(10L, 1L, 1);
        Question q1 = buildQuestion(20L, "5");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1));
        when(userAnswerRepository.findFirstByUserIdAndQuestionId(1L, 20L))
                .thenReturn(Optional.of(correctAnswer(q1)));

        HintDTO hint = service.getHint(EMAIL, 1L, 10L);

        assertThat(hint.getWrongQuestionIds()).isEmpty();
    }

    // ── getUserProgress ──────────────────────────────────────────────────────

    @Test
    void getUserProgress_returnsDTO() {
        User user = buildUser();
        UserProgress progress = buildProgress(1L);
        UserProgressDTO dto = new UserProgressDTO();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(mapperService.userProgressToDTO(progress)).thenReturn(dto);

        assertThat(service.getUserProgress(EMAIL, 1L)).isSameAs(dto);
    }

    @Test
    void getUserProgresses_returnsMappedList() {
        User user = buildUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userProgressRepository.findByUserId(1L)).thenReturn(List.of(buildProgress(1L), buildProgress(2L)));
        when(mapperService.userProgressToDTO(any())).thenReturn(new UserProgressDTO());

        assertThat(service.getUserProgresses(EMAIL)).hasSize(2);
    }

    // ── calculateTreasureCoordinates ─────────────────────────────────────────

    @Test
    void calculateTreasureCoordinates_returnsCoords_whenUnlocked() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L);
        progress.setTreasureHunt(hunt);
        progress.setIsTreasureUnlocked(true);

        CoordinateCalculationService.CalculatedCoordinates coords =
                new CoordinateCalculationService.CalculatedCoordinates(47.5, -1.5);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));
        when(coordinateService.calculateCoordinates(1L, hunt)).thenReturn(coords);

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateTreasureCoordinates(EMAIL, 1L);

        assertThat(result.getLatitude()).isEqualTo(47.5);
    }

    @Test
    void calculateTreasureCoordinates_throws_whenLocked() {
        User user = buildUser();
        UserProgress progress = buildProgress(1L);
        progress.setIsTreasureUnlocked(false);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));

        assertThatThrownBy(() -> service.calculateTreasureCoordinates(EMAIL, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not yet unlocked");
    }

    // ── validateCode ─────────────────────────────────────────────────────────

    @Test
    void validateCode_marksCompleted_whenCodeCorrect() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        hunt.setAccessCode("ABCD1234");
        UserProgress progress = buildProgress(1L);
        progress.setIsTreasureUnlocked(true);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));

        service.validateCode(EMAIL, 1L, "ABCD1234");

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCompleted()).isTrue();
        assertThat(captor.getValue().getCompletedAt()).isNotNull();
    }

    @Test
    void validateCode_throws_whenCodeWrong() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        hunt.setAccessCode("ABCD1234");
        UserProgress progress = buildProgress(1L);
        progress.setIsTreasureUnlocked(true);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));

        assertThatThrownBy(() -> service.validateCode(EMAIL, 1L, "ZZZZZZZZ"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Code incorrect");
    }

    @Test
    void validateCode_throws_whenTreasureNotUnlocked() {
        User user = buildUser();
        TreasureHunt hunt = buildHunt(1L);
        UserProgress progress = buildProgress(1L);
        progress.setIsTreasureUnlocked(false);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(treasureHuntRepository.findById(1L)).thenReturn(Optional.of(hunt));
        when(userProgressRepository.findByUserIdAndTreasureHuntId(1L, 1L)).thenReturn(Optional.of(progress));

        assertThatThrownBy(() -> service.validateCode(EMAIL, 1L, "ABCD1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not yet unlocked");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private UserAnswer correctAnswer(Question question) {
        UserAnswer a = new UserAnswer();
        a.setQuestion(question);
        a.setIsCorrect(true);
        return a;
    }
}
