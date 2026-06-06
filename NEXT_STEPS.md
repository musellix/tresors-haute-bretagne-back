# Prochaines étapes — Backend

## 🐛 BUGS À CORRIGER EN PRIORITÉ

### Dialogues affichés en double
**Symptôme** : Les dialogues semblent tous en double, ou il faut cliquer deux fois sur "Suivant" à chaque fois
**Contexte** :
- Après nettoyage des dialogues dupliqués en BDD (gardé uniquement MIN(id) pour chaque combinaison step_id/dialogue_order/text/korrigan_id)
- Backend modifié pour trier les dialogues par `dialogueOrder` dans MapperService.java (lignes 57-59)
- Frontend modifié pour recharger la progression après "Recommencer" (play.tsx ligne 191)
- Les dialogues sont bien dans l'ordre dans la BDD (vérification faite)

**À vérifier** :
1. Est-ce que l'API retourne vraiment les dialogues triés correctement ?
2. Y a-t-il encore des doublons cachés dans la BDD ?
3. Le compteur de dialogues dans DialogueView affiche-t-il le bon nombre total ?
4. L'index dialogueIndex se comporte-t-il correctement dans play.tsx ?
5. Les dialogues sont-ils dupliqués côté React (re-render ou state mal géré) ?

**Commandes debug utiles** :
```bash
# Vérifier les dialogues pour step 1
docker exec -i tresors-postgres psql -U postgres -d tresors_db -c "SELECT d.id, d.dialogue_order, LEFT(d.text, 60) FROM dialogues d JOIN steps s ON d.step_id = s.id WHERE s.treasure_hunt_id = 1 AND s.step_order = 1 ORDER BY d.dialogue_order;"

# Tester l'API directement
curl http://localhost:8080/api/treasure-hunts/1/steps -H "Authorization: Bearer <token>"
```

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
