# API REST Documentation

## Base URL
`http://localhost:8080/api`

## Endpoints

### Treasure Hunts

#### Get all active treasure hunts
```
GET /treasure-hunts
Response: List<TreasureHuntDTO>
```

#### Get treasure hunt by ID
```
GET /treasure-hunts/{id}
Response: TreasureHuntDTO
```

#### Get treasure hunts by theme
```
GET /treasure-hunts/theme/{themeId}
Response: List<TreasureHuntDTO>
```

#### Get all steps in a treasure hunt
```
GET /treasure-hunts/{huntId}/steps
Response: List<StepDTO>
```

#### Get specific step
```
GET /treasure-hunts/steps/{stepId}
Response: StepDTO
```

### Korrigans

#### Get all korrigans
```
GET /korrigans
Response: List<KorriganDTO>
```

#### Get korrigan by ID
```
GET /korrigans/{id}
Response: KorriganDTO
```

#### Get themes for a korrigan
```
GET /korrigans/{id}/themes
Response: List<ThemeDTO>
```

#### Create a new korrigan
```
POST /korrigans
Body: {
  "name": "Gribouille",
  "description": "Un korrigan facétieux...",
  "imageUrl": "https://..."
}
Response: KorriganDTO
```

### Themes

#### Get all themes
```
GET /themes
Response: List<ThemeDTO>
```

#### Get theme by ID
```
GET /themes/{id}
Response: ThemeDTO
```

#### Create a new theme
```
POST /themes
Body: {
  "name": "Les Merveilles de la Forêt",
  "description": "...",
  "imageUrl": "https://...",
  "korriganId": 1
}
Response: ThemeDTO
```

### User Progress

#### Start a treasure hunt
```
POST /user-progress/start/{userId}/{treasureHuntId}
Response: UserProgressDTO
```

#### Get user progress for a hunt
```
GET /user-progress/{userId}/{treasureHuntId}
Response: UserProgressDTO
```

#### Get all progress for a user
```
GET /user-progress/{userId}
Response: List<UserProgressDTO>
```

#### Submit an answer to a question
```
POST /user-progress/{userId}/answer
Body: {
  "questionId": 1,
  "answer": "5"
}
Response: AnswerFeedbackDTO {
  "questionId": 1,
  "isCorrect": true,
  "explanation": "1830 → 8-3 = 5",
  "userAnswer": "5"
}
```

#### Check and unlock treasure
```
POST /user-progress/{userId}/{treasureHuntId}/check-unlock
Checks if all questions are answered correctly and unlocks treasure
Response: 200 OK
```

#### Get treasure coordinates
```
GET /user-progress/{userId}/{treasureHuntId}/treasure-coordinates
Only available if treasure is unlocked
Response: TreasureCoordinatesDTO {
  "latitude": 47.7216,
  "longitude": -1.8523
}
```

#### Advance to next step
```
POST /user-progress/{userId}/{treasureHuntId}/advance-step
Response: UserProgressDTO
```

### Users

#### Get user by ID
```
GET /users/{id}
Response: UserDTO
```

#### Register new user
```
POST /users/register
Body: {
  "email": "user@example.com",
  "name": "John Doe",
  "avatarUrl": "https://..."
}
Response: UserDTO
```

#### Get user by email
```
GET /users/email/{email}
Response: UserDTO
```

## Workflow Example

### 1. User starts a treasure hunt
```
POST /user-progress/start/1/1
→ Returns UserProgressDTO with currentStep = 1
```

### 2. User sees questions for step 1
```
GET /treasure-hunts/steps/{step1Id}
→ Returns StepDTO with list of dialogues and questions
```

### 3. User submits answers
```
POST /user-progress/1/answer
Body: { "questionId": 1, "answer": "5" }
→ Returns AnswerFeedbackDTO with isCorrect and explanation
```

### 4. User completes all questions
```
POST /user-progress/1/1/check-unlock
→ Checks if all questions are correct
→ If yes, unlocks next step or final treasure
```

### 5. User gets treasure coordinates
```
GET /user-progress/1/1/treasure-coordinates
→ Returns TreasureCoordinatesDTO with final GPS coordinates
```

## Data Models

### TreasureHuntDTO
```json
{
  "id": 1,
  "title": "Chasse aux Pierres Magiques",
  "description": "...",
  "theme": { ThemeDTO },
  "finalLatitude": 48.1234,
  "finalLongitude": -1.5678,
  "treasureImageUrl": "https://...",
  "isActive": true,
  "steps": [ StepDTO ]
}
```

### StepDTO
```json
{
  "id": 1,
  "stepOrder": 1,
  "title": "Le Chêne Ancien",
  "description": "...",
  "latitude": 48.1111,
  "longitude": -1.5111,
  "radiusMeters": 100,
  "dialogues": [ DialogueDTO ],
  "questions": [ QuestionDTO ]
}
```

### QuestionDTO
```json
{
  "id": 1,
  "questionOrder": 1,
  "questionText": "Combien d'anneaux...",
  "questionType": "SHORT_TEXT",
  "explanation": "Un grand chêne peut vivre..."
}
```

### DialogueDTO
```json
{
  "id": 1,
  "dialogueOrder": 1,
  "text": "Bienvenue jeune voyageur...",
  "audioUrl": null,
  "korrigan": { KorriganDTO }
}
```

### UserProgressDTO
```json
{
  "id": 1,
  "userId": 1,
  "treasureHuntId": 1,
  "currentStep": 2,
  "isCompleted": false,
  "isTreasureUnlocked": false,
  "startedAt": "2024-06-03T10:00:00",
  "completedAt": null
}
```

## Coordinate Calculation

The final treasure coordinates are calculated based on user answers to questions.

Example formula from PDF:
- N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0'

Where:
- A, B, C, D are numeric values extracted from correct answers
- Values are substituted into the formula to generate final coordinates

This allows each treasure hunt to have dynamic coordinates based on puzzle solutions.
