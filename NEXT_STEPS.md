# Prochaines étapes — Backend

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
- **Pagination** : `GET /admin/users` → `Page<UserDTO>` (param `page`, `size`, `sort` ; défaut 20/page trié par id)
- **Vérification GPS serveur** : `POST /user-progress/{huntId}/steps/{stepId}/check-proximity` — Haversine, retourne `{ withinRange, distanceMeters, radiusMeters }`
- **Flux de jeu complet** (refonte UserProgress) :
  - `POST /user-progress/{huntId}/steps/{stepId}/submit-answers` — soumission groupée, upsert, avance d'étape ou débloque le trésor
  - `GET /user-progress/{huntId}/steps/{stepId}/hint` — retourne les `questionId` incorrects/manquants
  - `POST /user-progress/{huntId}/validate-code` — valide le code final 8 chars → marque la chasse terminée
  - `{userId}` supprimé de toutes les URLs → `@AuthenticationPrincipal`
  - `accessCode` ajouté sur `TreasureHunt`
  - Contrainte `UNIQUE (user_id, question_id)` sur `user_answers`
