package com.tresorshautebretagne.userProgress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tresorshautebretagne.config.JwtService;
import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import com.tresorshautebretagne.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProgressController.class)
@Import(UserProgressControllerTest.TestSecurityConfig.class)
class UserProgressControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testChain(HttpSecurity http) throws Exception {
            return http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                    .build();
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserProgressService userProgressService;
    @MockBean JwtService jwtService;
    @MockBean UserRepository userRepository;

    private static final String EMAIL = "user@test.com";

    private UserProgressDTO buildProgressDTO(Long huntId, int step) {
        UserProgressDTO dto = new UserProgressDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTreasureHuntId(huntId);
        dto.setCurrentStep(step);
        dto.setIsCompleted(false);
        dto.setIsTreasureUnlocked(false);
        dto.setStartedAt("2024-06-01T10:00:00");
        return dto;
    }

    @Test
    @WithMockUser(username = EMAIL)
    void startTreasureHunt_returns200() throws Exception {
        when(userProgressService.startTreasureHunt(EMAIL, 1L)).thenReturn(buildProgressDTO(1L, 1));

        mockMvc.perform(post("/user-progress/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStep").value(1));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getUserProgresses_returns200WithList() throws Exception {
        when(userProgressService.getUserProgresses(EMAIL))
                .thenReturn(List.of(buildProgressDTO(1L, 1), buildProgressDTO(2L, 1)));

        mockMvc.perform(get("/user-progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getUserProgress_returns200() throws Exception {
        when(userProgressService.getUserProgress(EMAIL, 1L)).thenReturn(buildProgressDTO(1L, 2));

        mockMvc.perform(get("/user-progress/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStep").value(2));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void submitAnswers_returns200WithResult() throws Exception {
        SubmitAnswersRequest.AnswerItem item = new SubmitAnswersRequest.AnswerItem();
        item.setQuestionId(1L);
        item.setAnswer("5");
        SubmitAnswersRequest request = new SubmitAnswersRequest();
        request.setAnswers(List.of(item));

        SubmitAnswersResultDTO result = new SubmitAnswersResultDTO();
        result.setAllCorrect(true);

        when(userProgressService.submitAnswers(EMAIL, 1L, 10L, request.getAnswers())).thenReturn(result);

        mockMvc.perform(post("/user-progress/1/steps/10/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allCorrect").value(true));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getHint_returns200WithWrongQuestionIds() throws Exception {
        HintDTO hint = new HintDTO();
        hint.setWrongQuestionIds(List.of(2L, 5L));

        when(userProgressService.getHint(EMAIL, 1L, 10L)).thenReturn(hint);

        mockMvc.perform(get("/user-progress/1/steps/10/hint"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wrongQuestionIds.length()").value(2))
                .andExpect(jsonPath("$.wrongQuestionIds[0]").value(2));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getTreasureCoordinates_returns200() throws Exception {
        CoordinateCalculationService.CalculatedCoordinates coords =
                new CoordinateCalculationService.CalculatedCoordinates(47.725333, -1.367167);

        when(userProgressService.calculateTreasureCoordinates(EMAIL, 1L)).thenReturn(coords);

        mockMvc.perform(get("/user-progress/1/treasure-coordinates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(47.725333))
                .andExpect(jsonPath("$.longitude").value(-1.367167));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void getTreasureCoordinates_returns500_whenLocked() throws Exception {
        when(userProgressService.calculateTreasureCoordinates(EMAIL, 1L))
                .thenThrow(new RuntimeException("Treasure not yet unlocked"));

        mockMvc.perform(get("/user-progress/1/treasure-coordinates"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void validateCode_returns200_whenCodeCorrect() throws Exception {
        ValidateCodeRequest request = new ValidateCodeRequest();
        request.setCode("ABCD1234");

        doNothing().when(userProgressService).validateCode(EMAIL, 1L, "ABCD1234");

        mockMvc.perform(post("/user-progress/1/validate-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void validateCode_returns500_whenCodeWrong() throws Exception {
        ValidateCodeRequest request = new ValidateCodeRequest();
        request.setCode("ZZZZZZZZ");

        doThrow(new RuntimeException("Code incorrect"))
                .when(userProgressService).validateCode(EMAIL, 1L, "ZZZZZZZZ");

        mockMvc.perform(post("/user-progress/1/validate-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}
