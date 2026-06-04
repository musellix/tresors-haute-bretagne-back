# Prochaines étapes — Backend

## 🟡 Secondaire

### 4. Vérification GPS côté serveur *(selon niveau de sécurité voulu)*
Actuellement le frontend décide si le joueur est assez proche d'une étape.
Ajouter un endpoint `POST /user-progress/{huntId}/check-proximity` qui reçoit
`{ latitude, longitude }` et vérifie la distance par rapport à `Step.radiusMeters`
(formule de Haversine ou `CoordinateCalculationService`).


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
- **Tests auth & admin** : `AuthServiceTest` (17), `AuthControllerTest` (13), `AdminServiceTest` (19) — 126 tests total, 0 échec
- **GET admin** : `GET /admin/treasure-hunts`, `GET /admin/steps/{stepId}/dialogues`, `GET /admin/steps/{stepId}/questions`
- **Flux de jeu complet** (refonte UserProgress) :
  - `POST /user-progress/{huntId}/steps/{stepId}/submit-answers` — soumission groupée, upsert (bug #1 corrigé), avance d'étape ou débloque le trésor automatiquement
  - `GET /user-progress/{huntId}/steps/{stepId}/hint` — retourne les `questionId` incorrects/manquants
  - `POST /user-progress/{huntId}/validate-code` — valide le code final 8 chars → marque la chasse terminée
  - `{userId}` supprimé de toutes les URLs → `@AuthenticationPrincipal` (bug #2 corrigé)
  - `accessCode` ajouté sur `TreasureHunt` (schéma + entité + admin DTO + AdminService)
  - Contrainte `UNIQUE (user_id, question_id)` sur `user_answers`
