package com.tresorshautebretagne.shared.service;

import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepDTO;
import com.tresorshautebretagne.treasureHunt.step.StepContentItemDTO;
import com.tresorshautebretagne.treasureHunt.dialogue.Dialogue;
import com.tresorshautebretagne.treasureHunt.dialogue.DialogueDTO;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionDTO;
import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.korrigan.KorriganDTO;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeDTO;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserDTO;
import com.tresorshautebretagne.userProgress.UserProgress;
import com.tresorshautebretagne.userProgress.UserProgressDTO;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswer;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswerDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MapperService {

    public ThemeDTO themeToDTO(Theme theme) {
        ThemeDTO dto = new ThemeDTO();
        dto.setId(theme.getId());
        dto.setName(theme.getName());
        dto.setDescription(theme.getDescription());
        dto.setImageUrl(theme.getImageUrl());
        dto.setKorriganId(theme.getKorrigan().getId());
        dto.setKorrigan(korriganToDTO(theme.getKorrigan()));
        return dto;
    }

    public KorriganDTO korriganToDTO(Korrigan korrigan) {
        KorriganDTO dto = new KorriganDTO();
        dto.setId(korrigan.getId());
        dto.setName(korrigan.getName());
        dto.setDescription(korrigan.getDescription());
        dto.setImageUrl(korrigan.getImageUrl());
        return dto;
    }

    public StepDTO stepToDTO(Step step) {
        StepDTO dto = new StepDTO();
        dto.setId(step.getId());
        dto.setStepOrder(step.getStepOrder());
        dto.setTitle(step.getTitle());
        dto.setDescription(step.getDescription());
        dto.setLatitude(step.getLatitude());
        dto.setLongitude(step.getLongitude());
        dto.setRadiusMeters(step.getRadiusMeters());
        
        List<DialogueDTO> dialogueDTOs = step.getDialogues().stream()
                .sorted(Comparator.comparing(Dialogue::getDialogueOrder))
                .map(this::dialogueToDTO)
                .collect(Collectors.toList());
        dto.setDialogues(dialogueDTOs);

        List<QuestionDTO> questionDTOs = step.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getQuestionOrder))
                .map(this::questionToDTO)
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);

        // Create unified content list ordered by contentOrder
        List<StepContentItemDTO> content = new ArrayList<>();
        dialogueDTOs.forEach(d -> content.add(StepContentItemDTO.fromDialogue(d)));
        questionDTOs.forEach(q -> content.add(StepContentItemDTO.fromQuestion(q)));
        content.sort(Comparator.comparing(StepContentItemDTO::getContentOrder));
        dto.setContent(content);

        return dto;
    }

    public DialogueDTO dialogueToDTO(Dialogue dialogue) {
        DialogueDTO dto = new DialogueDTO();
        dto.setId(dialogue.getId());
        dto.setDialogueOrder(dialogue.getDialogueOrder());
        dto.setContentOrder(dialogue.getContentOrder());
        dto.setText(dialogue.getText());
        dto.setAudioUrl(dialogue.getAudioUrl());
        dto.setKorrigan(dialogue.getKorrigan() != null ? korriganToDTO(dialogue.getKorrigan()) : null);
        return dto;
    }

    public QuestionDTO questionToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuestionOrder(question.getQuestionOrder());
        dto.setContentOrder(question.getContentOrder());
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
        dto.setExplanation(question.getExplanation());
        return dto;
    }

    public UserProgressDTO userProgressToDTO(UserProgress progress) {
        UserProgressDTO dto = new UserProgressDTO();
        dto.setId(progress.getId());
        dto.setUserId(progress.getUser().getId());
        dto.setTreasureHuntId(progress.getTreasureHunt().getId());
        dto.setCurrentStep(progress.getCurrentStep());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setIsTreasureUnlocked(progress.getIsTreasureUnlocked());
        dto.setStartedAt(progress.getStartedAt().toString());
        dto.setCompletedAt(progress.getCompletedAt() != null ? progress.getCompletedAt().toString() : null);
        return dto;
    }

    public UserAnswerDTO userAnswerToDTO(UserAnswer answer) {
        UserAnswerDTO dto = new UserAnswerDTO();
        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setAnswer(answer.getAnswer());
        dto.setIsCorrect(answer.getIsCorrect());
        return dto;
    }

    public UserDTO userToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
