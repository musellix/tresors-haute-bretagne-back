package com.tresorshautebretagne.userProgress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProgressController.class)
class UserProgressControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserProgressService userProgressService;
    @MockBean QuestionRepository questionRepository;

    private UserProgressDTO buildProgressDTO(Long userId, Long huntId, int step) {
        UserProgressDTO dto = new UserProgressDTO();
        dto.setId(1L);
        dto.setUserId(userId);
        dto.setTreasureHuntId(huntId);
        dto.setCurrentStep(step);
        dto.setIsCompleted(false);
        dto.setIsTreasureUnlocked(false);
        dto.setStartedAt("2024-06-01T10:00:00");
        return dto;
    }

    @Test
    void startTreasureHunt_returns200WithProgress() throws Exception {
        UserProgressDTO dto = buildProgressDTO(1L, 1L, 1);
        when(userProgressService.startTreasureHunt(1L, 1L)).thenReturn(dto);

        mockMvc.perform(post("/user-progress/start/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.treasureHuntId").value(1))
                .andExpect(jsonPath("$.currentStep").value(1));
    }

    @Test
    void getUserProgress_returns200() throws Exception {
        UserProgressDTO dto = buildProgressDTO(1L, 1L, 2);
        when(userProgressService.getUserProgress(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/user-progress/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.currentStep").value(2));
    }

    @Test
    void getUserProgresses_returns200WithList() throws Exception {
        when(userProgressService.getUserProgresses(1L))
                .thenReturn(List.of(buildProgressDTO(1L, 1L, 1), buildProgressDTO(1L, 2L, 1)));

        mockMvc.perform(get("/user-progress/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void submitAnswer_returnsCorrectFeedback_whenAnswerIsRight() throws Exception {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("5");
        question.setExplanation("Parce que 5");

        AnswerSubmitDTO request = new AnswerSubmitDTO();
        request.setQuestionId(1L);
        request.setAnswer("5");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        mockMvc.perform(post("/user-progress/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(1))
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.explanation").value("Parce que 5"))
                .andExpect(jsonPath("$.userAnswer").value("5"));
    }

    @Test
    void submitAnswer_returnsIncorrectFeedback_whenAnswerIsWrong() throws Exception {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("5");
        question.setExplanation("Parce que 5");

        AnswerSubmitDTO request = new AnswerSubmitDTO();
        request.setQuestionId(1L);
        request.setAnswer("3");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        mockMvc.perform(post("/user-progress/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(false))
                .andExpect(jsonPath("$.userAnswer").value("3"));
    }

    @Test
    void submitAnswer_isCaseInsensitive_returnsCorrect() throws Exception {
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer("Forêt");
        question.setExplanation("La forêt");

        AnswerSubmitDTO request = new AnswerSubmitDTO();
        request.setQuestionId(1L);
        request.setAnswer("forêt");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        mockMvc.perform(post("/user-progress/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    void checkAndUnlockTreasure_returns200() throws Exception {
        doNothing().when(userProgressService).checkAndUnlockTreasure(1L, 1L);

        mockMvc.perform(post("/user-progress/1/1/check-unlock"))
                .andExpect(status().isOk());
    }

    @Test
    void getTreasureCoordinates_returns200WithCoords_whenUnlocked() throws Exception {
        CoordinateCalculationService.CalculatedCoordinates coords =
                new CoordinateCalculationService.CalculatedCoordinates(47.725333, -1.367167);

        when(userProgressService.calculateTreasureCoordinates(1L, 1L)).thenReturn(coords);

        mockMvc.perform(get("/user-progress/1/1/treasure-coordinates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(47.725333))
                .andExpect(jsonPath("$.longitude").value(-1.367167));
    }

    @Test
    void getTreasureCoordinates_returns500_whenTreasureLocked() throws Exception {
        when(userProgressService.calculateTreasureCoordinates(1L, 1L))
                .thenThrow(new RuntimeException("Treasure not yet unlocked"));

        mockMvc.perform(get("/user-progress/1/1/treasure-coordinates"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void advanceStep_returns200WithUpdatedProgress() throws Exception {
        UserProgressDTO dto = buildProgressDTO(1L, 1L, 2);

        doNothing().when(userProgressService).advanceStep(1L, 1L);
        when(userProgressService.getUserProgress(1L, 1L)).thenReturn(dto);

        mockMvc.perform(post("/user-progress/1/1/advance-step"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStep").value(2));
    }
}
