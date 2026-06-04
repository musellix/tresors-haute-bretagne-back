package com.tresorshautebretagne.admin;

import com.tresorshautebretagne.admin.dto.*;
import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.korrigan.KorriganDTO;
import com.tresorshautebretagne.korrigan.KorriganRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.theme.ThemeDTO;
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
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserDTO;
import com.tresorshautebretagne.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KorriganRepository korriganRepository;
    private final ThemeRepository themeRepository;
    private final TreasureHuntRepository treasureHuntRepository;
    private final StepRepository stepRepository;
    private final DialogueRepository dialogueRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    // ── Korrigans ────────────────────────────────────────────────────────────

    @Transactional
    public KorriganDTO createKorrigan(KorriganRequest req) {
        Korrigan k = new Korrigan();
        k.setName(req.getName());
        k.setDescription(req.getDescription());
        k.setImageUrl(req.getImageUrl());
        return mapperService.korriganToDTO(korriganRepository.save(k));
    }

    @Transactional
    public KorriganDTO updateKorrigan(Long id, KorriganRequest req) {
        Korrigan k = korriganRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korrigan introuvable : " + id));
        k.setName(req.getName());
        k.setDescription(req.getDescription());
        k.setImageUrl(req.getImageUrl());
        return mapperService.korriganToDTO(korriganRepository.save(k));
    }

    @Transactional
    public void deleteKorrigan(Long id) {
        korriganRepository.deleteById(id);
    }

    // ── Thèmes ───────────────────────────────────────────────────────────────

    @Transactional
    public ThemeDTO createTheme(ThemeRequest req) {
        Korrigan k = korriganRepository.findById(req.getKorriganId())
                .orElseThrow(() -> new RuntimeException("Korrigan introuvable : " + req.getKorriganId()));
        Theme t = new Theme();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setImageUrl(req.getImageUrl());
        t.setKorrigan(k);
        return mapperService.themeToDTO(themeRepository.save(t));
    }

    @Transactional
    public ThemeDTO updateTheme(Long id, ThemeRequest req) {
        Theme t = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thème introuvable : " + id));
        Korrigan k = korriganRepository.findById(req.getKorriganId())
                .orElseThrow(() -> new RuntimeException("Korrigan introuvable : " + req.getKorriganId()));
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setImageUrl(req.getImageUrl());
        t.setKorrigan(k);
        return mapperService.themeToDTO(themeRepository.save(t));
    }

    @Transactional
    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }

    // ── Chasses au trésor ────────────────────────────────────────────────────

    @Transactional
    public TreasureHuntDTO createTreasureHunt(TreasureHuntRequest req) {
        Theme theme = themeRepository.findById(req.getThemeId())
                .orElseThrow(() -> new RuntimeException("Thème introuvable : " + req.getThemeId()));
        TreasureHunt hunt = new TreasureHunt();
        applyHuntRequest(hunt, req, theme);
        hunt.setSteps(List.of());
        return toHuntDTO(treasureHuntRepository.save(hunt));
    }

    @Transactional
    public TreasureHuntDTO updateTreasureHunt(Long id, TreasureHuntRequest req) {
        TreasureHunt hunt = treasureHuntRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable : " + id));
        Theme theme = themeRepository.findById(req.getThemeId())
                .orElseThrow(() -> new RuntimeException("Thème introuvable : " + req.getThemeId()));
        applyHuntRequest(hunt, req, theme);
        return toHuntDTO(treasureHuntRepository.save(hunt));
    }

    @Transactional
    public void deleteTreasureHunt(Long id) {
        treasureHuntRepository.deleteById(id);
    }

    @Transactional
    public TreasureHuntDTO toggleActive(Long id) {
        TreasureHunt hunt = treasureHuntRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable : " + id));
        hunt.setIsActive(!hunt.getIsActive());
        return toHuntDTO(treasureHuntRepository.save(hunt));
    }

    // ── Étapes ───────────────────────────────────────────────────────────────

    @Transactional
    public StepDTO createStep(Long huntId, StepRequest req) {
        TreasureHunt hunt = treasureHuntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Chasse introuvable : " + huntId));
        Step step = new Step();
        applyStepRequest(step, req, hunt);
        step.setDialogues(List.of());
        step.setQuestions(List.of());
        return mapperService.stepToDTO(stepRepository.save(step));
    }

    @Transactional
    public StepDTO updateStep(Long stepId, StepRequest req) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Étape introuvable : " + stepId));
        applyStepRequest(step, req, step.getTreasureHunt());
        return mapperService.stepToDTO(stepRepository.save(step));
    }

    @Transactional
    public void deleteStep(Long stepId) {
        stepRepository.deleteById(stepId);
    }

    // ── Dialogues ────────────────────────────────────────────────────────────

    @Transactional
    public DialogueDTO createDialogue(Long stepId, DialogueRequest req) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Étape introuvable : " + stepId));
        Korrigan k = korriganRepository.findById(req.getKorriganId())
                .orElseThrow(() -> new RuntimeException("Korrigan introuvable : " + req.getKorriganId()));
        Dialogue d = new Dialogue();
        d.setStep(step);
        d.setKorrigan(k);
        d.setDialogueOrder(req.getDialogueOrder());
        d.setText(req.getText());
        d.setAudioUrl(req.getAudioUrl());
        return mapperService.dialogueToDTO(dialogueRepository.save(d));
    }

    @Transactional
    public DialogueDTO updateDialogue(Long dialogueId, DialogueRequest req) {
        Dialogue d = dialogueRepository.findById(dialogueId)
                .orElseThrow(() -> new RuntimeException("Dialogue introuvable : " + dialogueId));
        Korrigan k = korriganRepository.findById(req.getKorriganId())
                .orElseThrow(() -> new RuntimeException("Korrigan introuvable : " + req.getKorriganId()));
        d.setKorrigan(k);
        d.setDialogueOrder(req.getDialogueOrder());
        d.setText(req.getText());
        d.setAudioUrl(req.getAudioUrl());
        return mapperService.dialogueToDTO(dialogueRepository.save(d));
    }

    @Transactional
    public void deleteDialogue(Long dialogueId) {
        dialogueRepository.deleteById(dialogueId);
    }

    // ── Questions ────────────────────────────────────────────────────────────

    @Transactional
    public QuestionDTO createQuestion(Long stepId, QuestionRequest req) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Étape introuvable : " + stepId));
        Question q = new Question();
        q.setStep(step);
        applyQuestionRequest(q, req);
        return mapperService.questionToDTO(questionRepository.save(q));
    }

    @Transactional
    public QuestionDTO updateQuestion(Long questionId, QuestionRequest req) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question introuvable : " + questionId));
        applyQuestionRequest(q, req);
        return mapperService.questionToDTO(questionRepository.save(q));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    // ── Utilisateurs ─────────────────────────────────────────────────────────

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapperService::userToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateRole(Long userId, UpdateRoleRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));
        user.setRole(req.getRole());
        return mapperService.userToDTO(userRepository.save(user));
    }

    @Transactional
    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void applyHuntRequest(TreasureHunt hunt, TreasureHuntRequest req, Theme theme) {
        hunt.setTitle(req.getTitle());
        hunt.setDescription(req.getDescription());
        hunt.setTheme(theme);
        hunt.setFinalLatitude(req.getFinalLatitude());
        hunt.setFinalLongitude(req.getFinalLongitude());
        hunt.setTreasureImageUrl(req.getTreasureImageUrl());
        hunt.setCoordinateFormula(req.getCoordinateFormula());
        hunt.setAccessCode(req.getAccessCode());
        hunt.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
    }

    private void applyStepRequest(Step step, StepRequest req, TreasureHunt hunt) {
        step.setTreasureHunt(hunt);
        step.setStepOrder(req.getStepOrder());
        step.setTitle(req.getTitle());
        step.setDescription(req.getDescription());
        step.setLatitude(req.getLatitude());
        step.setLongitude(req.getLongitude());
        step.setRadiusMeters(req.getRadiusMeters() != null ? req.getRadiusMeters() : 50);
    }

    private void applyQuestionRequest(Question q, QuestionRequest req) {
        q.setQuestionOrder(req.getQuestionOrder());
        q.setQuestionText(req.getQuestionText());
        q.setCorrectAnswer(req.getCorrectAnswer());
        q.setExplanation(req.getExplanation());
        q.setQuestionType(req.getQuestionType() != null ? req.getQuestionType() : "SHORT_TEXT");
    }

    private TreasureHuntDTO toHuntDTO(TreasureHunt hunt) {
        TreasureHuntDTO dto = new TreasureHuntDTO();
        dto.setId(hunt.getId());
        dto.setTitle(hunt.getTitle());
        dto.setDescription(hunt.getDescription());
        dto.setTheme(mapperService.themeToDTO(hunt.getTheme()));
        dto.setFinalLatitude(hunt.getFinalLatitude());
        dto.setFinalLongitude(hunt.getFinalLongitude());
        dto.setTreasureImageUrl(hunt.getTreasureImageUrl());
        dto.setIsActive(hunt.getIsActive());
        dto.setSteps(hunt.getSteps() == null ? List.of() :
                hunt.getSteps().stream().map(mapperService::stepToDTO).collect(Collectors.toList()));
        return dto;
    }
}
