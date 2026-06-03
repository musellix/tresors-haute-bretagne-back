# Architecture par modules (Domain-Driven)

## Structure - Option C : DTOs à côté des entités

Le projet est organisé par modules (domaines) plutôt que par couches techniques.
**Chaque DTO est placé directement avec son entité**, à plat (pas de sous-dossier `dto/`).

```
com.tresorshautebretagne/
│
├── treasureHunt/                   Module Chasse au trésor
│   ├── TreasureHunt.java
│   ├── TreasureHuntDTO.java        ← DTO à côté
│   ├── TreasureHuntRepository.java
│   ├── TreasureHuntService.java
│   ├── TreasureHuntController.java
│   │
│   ├── step/                       Entité secondaire
│   │   ├── Step.java
│   │   ├── StepDTO.java            ← DTO à côté
│   │   └── StepRepository.java
│   │
│   ├── dialogue/                   Entité secondaire
│   │   ├── Dialogue.java
│   │   ├── DialogueDTO.java        ← DTO à côté
│   │   └── DialogueRepository.java
│   │
│   └── question/                   Entité secondaire
│       ├── Question.java
│       ├── QuestionDTO.java        ← DTO à côté
│       └── QuestionRepository.java
│
├── korrigan/                       Module Korrigan
│   ├── Korrigan.java
│   ├── KorriganDTO.java            ← DTO à côté
│   ├── KorriganRepository.java
│   └── KorriganController.java
│
├── theme/                          Module Thème
│   ├── Theme.java
│   ├── ThemeDTO.java               ← DTO à côté
│   ├── ThemeRepository.java
│   └── ThemeController.java
│
├── user/                           Module Utilisateur
│   ├── User.java
│   ├── UserDTO.java                ← DTO à côté
│   ├── UserRepository.java
│   └── UserController.java
│
├── userProgress/                   Module Progression utilisateur
│   ├── UserProgress.java
│   ├── UserProgressDTO.java        ← DTO à côté
│   ├── AnswerSubmitDTO.java        ← DTO à côté
│   ├── AnswerFeedbackDTO.java      ← DTO à côté
│   ├── TreasureCoordinatesDTO.java ← DTO à côté
│   ├── UserProgressRepository.java
│   ├── UserProgressService.java
│   ├── UserProgressController.java
│   │
│   └── userAnswer/                 Entité secondaire
│       ├── UserAnswer.java
│       ├── UserAnswerDTO.java      ← DTO à côté
│       └── UserAnswerRepository.java
│
└── shared/                         Services partagés
    └── service/
        ├── MapperService.java       (Conversion Entity ↔ DTO)
        └── CoordinateCalculationService.java (Calcul coordonnées)
```

## Principes

1. **Par modules (domaines)**: Chaque module représente un domaine métier
2. **Structure plate**: Un seul fichier par type = pas de dossier (Entity, Repository, Controller)
3. **Sous-dossiers**: Seulement pour les entités secondaires (step/, dialogue/, question/, userAnswer/)
4. **DTOs groupés**: Tous les DTOs d'un module dans le dossier `dto/`
5. **Services partagés**: `shared/` pour les utilitaires transversaux

## Avantages

✅ **Cohérence**: Toute la logique d'un domaine au même endroit  
✅ **Scalabilité**: Facile d'ajouter de nouveaux modules  
✅ **Testabilité**: Chaque module peut être testé indépendamment  
✅ **Navigation**: Plus simple de trouver le code  
✅ **Maintenabilité**: Moins de fichiers, structure claire

## Exemple: Ajouter un nouveau module

Pour ajouter un module "achievements":

```
com.tresorshautebretagne/achievements/
├── Achievement.java
├── AchievementRepository.java
├── AchievementService.java
├── AchievementController.java
├── userAchievement/
│   ├── UserAchievement.java
│   └── UserAchievementRepository.java
└── dto/
    ├── AchievementDTO.java
    └── UserAchievementDTO.java
```

Puis importer les services/repositories depuis les autres modules au besoin.
