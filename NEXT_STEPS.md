# Prochaines étapes — Backend

## 🔴 Bugs critiques (à corriger en priorité)

### 1. `submitAnswer` bloque définitivement le joueur
**Fichier :** `src/main/java/com/tresorshautebretagne/userProgress/UserProgressService.java`

**Problème :** chaque soumission crée un nouveau `UserAnswer`. La vérification
`allQuestionsAnsweredCorrectly` fait `allMatch(isCorrect)` sur toutes les réponses
passées — si une tentative était fausse, le joueur ne peut jamais déverrouiller le
trésor même en répondant correctement ensuite.

**Fix :** upsert — remplacer la réponse existante (même `userId` + `questionId`)
au lieu d'en créer une nouvelle. Ajouter une méthode dans `UserAnswerRepository` :
```java
Optional<UserAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
```
Puis dans `submitAnswer` : chercher l'existante, la mettre à jour ou en créer une
nouvelle.

---

### 2. `userId` dans l'URL non vérifié
**Fichier :** `src/main/java/com/tresorshautebretagne/userProgress/UserProgressController.java`

**Problème :** `{userId}` dans les routes n'est pas comparé à l'utilisateur
connecté. N'importe quel user authentifié peut soumettre des réponses pour
quelqu'un d'autre en changeant l'URL.

**Fix :** remplacer `@PathVariable Long userId` par `@AuthenticationPrincipal User user`
et utiliser `user.getId()` directement dans le service.

---

## 🟠 Fonctionnalités manquantes (nécessaires pour le frontend admin)

### 3. GET admin manquants
**Fichier :** `src/main/java/com/tresorshautebretagne/admin/AdminTreasureHuntController.java`

À ajouter dans `AdminService` + `AdminTreasureHuntController` :
- `GET /admin/treasure-hunts` → toutes les chasses, actives **et** inactives
  (le `GET /treasure-hunts` existant ne retourne que les actives)
- `GET /admin/steps/{stepId}/dialogues` → liste des dialogues d'une étape
- `GET /admin/steps/{stepId}/questions` → liste des questions d'une étape

---

## 🟡 Secondaire

### 4. Vérification GPS côté serveur *(selon niveau de sécurité voulu)*
Actuellement le frontend décide si le joueur est assez proche d'une étape.
Ajouter un endpoint `POST /user-progress/{huntId}/check-proximity` qui reçoit
`{ latitude, longitude }` et vérifie la distance par rapport à `Step.radiusMeters`
(formule de Haversine ou `CoordinateCalculationService`).

### 5. Tests auth & admin
Aucun test pour `AuthService`, `AuthController`, `AdminService`. À écrire.

### 6. Pagination sur `GET /admin/users`
Retourne tous les utilisateurs d'un coup. Utiliser `Pageable` Spring Data.

---

## ✅ Déjà fait
- Entités + repositories + controllers GET (korrigans, thèmes, chasses, étapes, dialogues, questions, users, user-progress)
- Auth : register / login / Google OAuth / vérification email / refresh token / logout
- Rôles : `USER` / `ADMIN`, routes `/admin/**` protégées
- CRUD admin complet (korrigans, thèmes, chasses, étapes, dialogues, questions, users)
- Spring Security + JWT (7 jours) + refresh token rotatif (90 jours)
- CORS, GlobalExceptionHandler, BCrypt
- Tests unitaires pour tous les modules existants (avant auth/admin)
- **Flux de jeu complet** (refonte UserProgress) :
  - `POST /user-progress/{huntId}/steps/{stepId}/submit-answers` — soumission groupée, upsert (bug #1 corrigé), avance d'étape ou débloque le trésor automatiquement
  - `GET /user-progress/{huntId}/steps/{stepId}/hint` — retourne les `questionId` incorrects/manquants
  - `POST /user-progress/{huntId}/validate-code` — valide le code final 8 chars → marque la chasse terminée
  - `{userId}` supprimé de toutes les URLs → `@AuthenticationPrincipal` (bug #2 corrigé)
  - `accessCode` ajouté sur `TreasureHunt` (schéma + entité + admin DTO + AdminService)
  - Contrainte `UNIQUE (user_id, question_id)` sur `user_answers`
