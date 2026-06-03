package com.tresorshautebretagne.shared.service;

import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswer;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinateCalculationServiceTest {

    @Mock private UserAnswerRepository userAnswerRepository;
    @Mock private StepRepository stepRepository;
    @Mock private QuestionRepository questionRepository;

    @InjectMocks
    private CoordinateCalculationService service;

    private TreasureHunt buildHunt(String formula) {
        TreasureHunt hunt = new TreasureHunt();
        hunt.setId(1L);
        hunt.setFinalLatitude(48.0);
        hunt.setFinalLongitude(-1.5);
        hunt.setCoordinateFormula(formula);
        return hunt;
    }

    private Step buildStep(Long id) {
        Step step = new Step();
        step.setId(id);
        step.setStepOrder(1);
        return step;
    }

    private Question buildQuestion(Long id, int order) {
        Question q = new Question();
        q.setId(id);
        q.setQuestionOrder(order);
        q.setCorrectAnswer("dummy");
        return q;
    }

    private UserAnswer correctAnswer(String answer) {
        UserAnswer ua = new UserAnswer();
        ua.setAnswer(answer);
        ua.setIsCorrect(true);
        return ua;
    }

    @Test
    void calculateCoordinates_withNullFormula_returnsStoredCoords() {
        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, buildHunt(null));

        assertThat(result.getLatitude()).isEqualTo(48.0);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }

    @Test
    void calculateCoordinates_withBlankFormula_returnsStoredCoords() {
        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, buildHunt("   "));

        assertThat(result.getLatitude()).isEqualTo(48.0);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }

    @Test
    void calculateCoordinates_withSimpleTokens_returnsCalculatedCoords() {
        // Formula: N 47°4(A).5(B)0' / W 1°2(B).0(A)0'
        // A=3, B=2 → N 47°43.520' / W 1°22.030'
        // lat = 47 + 43.520/60 = 47.725333, lon = -(1 + 22.030/60) = -1.367167
        TreasureHunt hunt = buildHunt("N 47°4(A).5(B)0' / W 1°2(B).0(A)0'");
        Step step = buildStep(10L);
        Question q1 = buildQuestion(1L, 1);
        Question q2 = buildQuestion(2L, 2);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1, q2));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 1L)).thenReturn(List.of(correctAnswer("3")));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 2L)).thenReturn(List.of(correctAnswer("2")));

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, hunt);

        assertThat(result.getLatitude()).isEqualTo(47.725333);
        assertThat(result.getLongitude()).isEqualTo(-1.367167);
    }

    @Test
    void calculateCoordinates_withArithmeticOperations_returnsCalculatedCoords() {
        // Formula: N 47°4(Ax2).0(B+1)0' / W 1°20.0(A-1)0'
        // A=3, B=2 → N 47°46.030' / W 1°20.020'
        // lat = 47 + 46.030/60 = 47.767167, lon = -(1 + 20.020/60) = -1.333667
        TreasureHunt hunt = buildHunt("N 47°4(Ax2).0(B+1)0' / W 1°20.0(A-1)0'");
        Step step = buildStep(10L);
        Question q1 = buildQuestion(1L, 1);
        Question q2 = buildQuestion(2L, 2);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1, q2));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 1L)).thenReturn(List.of(correctAnswer("3")));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 2L)).thenReturn(List.of(correctAnswer("2")));

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, hunt);

        assertThat(result.getLatitude()).isEqualTo(47.767167);
        assertThat(result.getLongitude()).isEqualTo(-1.333667);
    }

    @Test
    void calculateCoordinates_withMissingVariable_fallsBackToStoredCoords() {
        // Formula references (B) but user only has answer for A
        TreasureHunt hunt = buildHunt("N 47°4(A).5(B)0' / W 1°20.030'");
        Step step = buildStep(10L);
        Question q1 = buildQuestion(1L, 1);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 1L)).thenReturn(List.of(correctAnswer("3")));

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, hunt);

        assertThat(result.getLatitude()).isEqualTo(48.0);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }

    @Test
    void calculateCoordinates_withIncorrectAnswer_variableSkipped_fallsBack() {
        // Variable A is not assigned because the answer is wrong
        TreasureHunt hunt = buildHunt("N 47°4(A).50' / W 1°20.030'");
        Step step = buildStep(10L);
        Question q1 = buildQuestion(1L, 1);

        UserAnswer wrongAnswer = new UserAnswer();
        wrongAnswer.setAnswer("5");
        wrongAnswer.setIsCorrect(false);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 1L)).thenReturn(List.of(wrongAnswer));

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, hunt);

        assertThat(result.getLatitude()).isEqualTo(48.0);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }

    @Test
    void calculateCoordinates_withNoAnswers_fallsBackToStoredCoords() {
        TreasureHunt hunt = buildHunt("N 47°4(A).50' / W 1°20.030'");
        Step step = buildStep(10L);
        Question q1 = buildQuestion(1L, 1);

        when(stepRepository.findByTreasureHuntIdOrderByStepOrder(1L)).thenReturn(List.of(step));
        when(questionRepository.findByStepIdOrderByQuestionOrder(10L)).thenReturn(List.of(q1));
        when(userAnswerRepository.findByUserIdAndQuestionId(1L, 1L)).thenReturn(List.of());

        CoordinateCalculationService.CalculatedCoordinates result =
                service.calculateCoordinates(1L, hunt);

        assertThat(result.getLatitude()).isEqualTo(48.0);
        assertThat(result.getLongitude()).isEqualTo(-1.5);
    }
}
