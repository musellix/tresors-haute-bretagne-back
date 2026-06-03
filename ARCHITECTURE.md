# Architecture par modules

## Structure

Le projet est organisé par modules (domaines) plutôt que par couches techniques.

```
com.tresorshautebretagne/
│
├── treasureHunt/                   Module Chasse au trésor
│   ├── TreasureHunt.java
│   ├── TreasureHuntRepository.java
│   ├── TreasureHuntService.java
│   ├── TreasureHuntController.java
│   │
│   ├── step/                       Entité secondaire
│   │   ├── Step.java
│   │   └── StepRepository.java
│   │
│   ├── dialogue/                   Entité secondaire
│   │   ├── Dialogue.java
│   │   └── DialogueRepository.java
│   │
│   ├── question/                   Entité secondaire
│   │   ├── Question.java
│   │   └── QuestionRepository.java
│   │
│   └── dto/                        DTOs du module
│       ├── TreasureHuntDTO.java
│       ├── StepDTO.java
│       ├── DialogueDTO.java
│       └── QuestionDTO.java
│
├── korrigan/                       Module Korrigan
│   ├── Korrigan.java
│   ├── KorriganRepository.java
│   ├── KorriganController.java
│   │
│   └── dto/
│       └── KorriganDTO.java
│
├── theme/                          Module Thème
│   ├── Theme.java
│   ├── ThemeRepository.java
│   ├── ThemeController.java
│   │
│   └── dto/
│       └── ThemeDTO.java
│
├── user/                           Module Utilisateur
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserController.java
│   │
│   └── dto/
│       └── UserDTO.java
│
├── userProgress/                   Module Progression utilisateur
│   ├── UserProgress.java
│   ├── UserProgressRepository.java
│   ├── UserProgressService.java
│   ├── UserProgressController.java
│   │
│   ├── userAnswer/                 Entité secondaire
│   │   ├── UserAnswer.java
│   │   └── UserAnswerRepository.java
│   │
│   └── dto/
│       ├── UserProgressDTO.java
│       ├── UserAnswerDTO.java
│       ├── AnswerSubmitDTO.java
│       ├── AnswerFeedbackDTO.java
│       └── TreasureCoordinatesDTO.java
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
