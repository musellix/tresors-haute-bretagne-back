package com.tresorshautebretagne.service;

import com.tresorshautebretagne.entity.Question;
import com.tresorshautebretagne.entity.Step;
import com.tresorshautebretagne.entity.TreasureHunt;
import com.tresorshautebretagne.entity.UserAnswer;
import com.tresorshautebretagne.repository.QuestionRepository;
import com.tresorshautebretagne.repository.StepRepository;
import com.tresorshautebretagne.repository.UserAnswerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoordinateCalculationService {

    private final UserAnswerRepository userAnswerRepository;
    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;

    /**
     * Calculate final treasure coordinates based on user answers to questions.
     * Example: If questions are numbered (A, B, C, D), extract numeric values
     * and use them in coordinate calculation formula.
     * 
     * For the PDF example:
     * - A = 5 (1830 → 8-3 = 5)
     * - B = 3 (12 clochers → 1+2 = 3)
     * - C = 7 (lavoir answer)
     * - D = 5 (tree answer)
     * 
     * Final coords: N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0'
     * = N 47°43.25(3x2)' / W 1°50.7(5+1)0'
     * = N 47°43.2556' / W 1°50.760'
     */
    public CalculatedCoordinates calculateCoordinates(Long userId, TreasureHunt hunt) {
        // Get all steps in order
        List<Step> steps = stepRepository.findByTreasureHuntIdOrderByStepOrder(hunt.getId());
        
        // Extract numeric values from answers (A, B, C, D, etc.)
        Map<Character, Integer> answerValues = extractAnswerValues(userId, steps);
        
        // Parse the coordinate formula from the hunt (if stored)
        // For now, use a hardcoded example - this should be stored in TreasureHunt
        CalculatedCoordinates coordinates = applyCoordinateFormula(answerValues, hunt);
        
        return coordinates;
    }

    private Map<Character, Integer> extractAnswerValues(Long userId, List<Step> steps) {
        Map<Character, Integer> values = new HashMap<>();
        char label = 'A';
        
        for (Step step : steps) {
            List<Question> questions = questionRepository.findByStepIdOrderByQuestionOrder(step.getId());
            
            for (Question question : questions) {
                List<UserAnswer> answers = userAnswerRepository.findByUserIdAndQuestionId(userId, question.getId());
                
                if (!answers.isEmpty() && answers.get(0).getIsCorrect()) {
                    // Extract numeric value from answer
                    Integer numericValue = extractNumericValue(answers.get(0).getAnswer());
                    if (numericValue != null) {
                        values.put(label, numericValue);
                        label++;
                    }
                }
            }
        }
        
        return values;
    }

    private Integer extractNumericValue(String answer) {
        try {
            // Try parsing the whole answer
            return Integer.parseInt(answer.trim());
        } catch (NumberFormatException e) {
            // Try extracting first number
            String numbers = answer.replaceAll("[^0-9]", "");
            if (!numbers.isEmpty()) {
                return Integer.parseInt(numbers.substring(0, 1));
            }
            return null;
        }
    }

    /**
     * Apply coordinate formula.
     * Example: N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0'
     * Replaces (B), (D), (Bx2), (C), (A+1) with actual values
     */
    private CalculatedCoordinates applyCoordinateFormula(Map<Character, Integer> values, TreasureHunt hunt) {
        // For now, use a simple formula - this should be flexible per hunt
        // Example: N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0'
        
        Integer a = values.getOrDefault('A', 0);
        Integer b = values.getOrDefault('B', 0);
        Integer c = values.getOrDefault('C', 0);
        Integer d = values.getOrDefault('D', 0);
        
        // N 47°4(B).2(D)(Bx2)'
        String latDegrees = "47";
        String latMinutes = "4" + b + ".2" + d + (b * 2);
        Double latitude = Double.parseDouble(latDegrees + (latMinutes.length() > 0 ? "." + latMinutes : ""));
        
        // W 1°(D)0.(C)(A+1)0'
        String lonDegrees = "1";
        String lonMinutes = d + "0." + c + (a + 1) + "0";
        Double longitude = -(Double.parseDouble(lonDegrees + (lonMinutes.length() > 0 ? "." + lonMinutes : "")));
        
        // Round to 6 decimal places
        latitude = Math.round(latitude * 1000000.0) / 1000000.0;
        longitude = Math.round(longitude * 1000000.0) / 1000000.0;
        
        return new CalculatedCoordinates(latitude, longitude);
    }

    @Data
    @AllArgsConstructor
    public static class CalculatedCoordinates {
        private Double latitude;
        private Double longitude;
    }
}
