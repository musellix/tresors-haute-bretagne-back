-- Import du parcours "Au bonheur des dames"
-- Ce script insère le parcours complet avec dialogues et questions entrelacés

-- korrigan_id nullable pour les encarts info (dialogues sans korrigan)
ALTER TABLE dialogues ALTER COLUMN korrigan_id DROP NOT NULL;

-- Insérer le thème et le korrigan (si pas déjà présents)
INSERT INTO korrigans (id, name, description, image_url, created_at) VALUES
(1, 'Queen Aman', 'Guide spirituelle des trésors de Haute-Bretagne', null, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO themes (id, name, description, image_url, korrigan_id, created_at) VALUES
(1, 'Histoire', 'Parcours historiques et patrimoniaux', null, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insérer le parcours
INSERT INTO treasure_hunts (id, title, description, theme_id, final_latitude, final_longitude, treasure_image_url, is_active, access_code, created_at) VALUES
(1, 'Au bonheur des dames', 'Découvrez Langon à travers des histoires de femmes et des monuments qui évoquent la vie et le passé de ce charmant petit bourg.', 1, 47.7216500, -1.8522833, null, true, 'K7M9P2X4', CURRENT_TIMESTAMP);

-- Insérer les étapes (step_order 0 = préambule affiché sur la fiche, 1-5 = étapes jouées)
INSERT INTO steps (id, treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters, created_at) VALUES
(6, 1, 0, 'Préambule', 'Introduction au parcours', 47.7205000, -1.8488833, 0, CURRENT_TIMESTAMP),
(1, 1, 1, 'Départ', 'Point de départ près de l''église', 47.7205000, -1.8488833, 50, CURRENT_TIMESTAMP),
(2, 1, 2, 'Agathe, Pierre et Paul', 'La chapelle Sainte-Agathe et l''église', 47.7206667, -1.8493333, 50, CURRENT_TIMESTAMP),
(3, 1, 3, 'Le lavoir', 'Rendez-vous avec les lavandières', 47.7189833, -1.8494833, 50, CURRENT_TIMESTAMP),
(4, 1, 4, 'Les Demoiselles', 'L''alignement mégalithique', 47.7213333, -1.8544833, 50, CURRENT_TIMESTAMP),
(5, 1, 5, 'La Cache', 'Le trésor final', 47.7216500, -1.8522833, 50, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 0 : PRÉAMBULE (affiché sur la fiche parcours, pas dans le jeu)
-- ========================================
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(6, 1, 'Bonjour tout le monde ! Pour cette chasse aux trésors, direction Langon, commune située au sud de l''Ille-et-Vilaine en Pays de Redon…. Là-bas pas de château fort, ni de bataille à vous raconter mais plutôt des histoires de femmes à travers des monuments et des sites qui évoquent la vie et le passé de ce charmant petit bourg labellisé Commune du Patrimoine Rural de Bretagne.', 1, 1, CURRENT_TIMESTAMP),
(6, 1, 'Un cache féministe peut-être ?', 2, 2, CURRENT_TIMESTAMP),
(6, 1, 'Pas forcément, mais bien souvent on oublie le rôle joué par les femmes au cours de l''histoire ou même tout simplement dans la vie quotidienne. Si tu veux en savoir plus, tu n''as qu''à me suivre….', 3, 3, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 1 : DÉPART (point GPS parking — dialogues post-carte)
-- ========================================
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(1, 1, 'Le mieux est de vous garer sur le petit parking près de l''église. Il vous permettra de rejoindre facilement votre véhicule à la fin du circuit. Dis-moi, Queen Aman, quelle est la première femme que nous allons rencontrer ?', 1, 1, CURRENT_TIMESTAMP),
(1, 1, 'Elle se prénomme Agathe… et elle sera accompagnée.', 2, 2, CURRENT_TIMESTAMP),
(1, 1, 'Ah, zut !', 3, 3, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 2 : AGATHE, PIERRE ET PAUL
-- ========================================
-- Dialogues + encarts info + 2 questions entrelacées (korrigan_id nullable pour les encarts)
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(2, 1, 'Mon cher Korry Gan, ici tu risques fort de te casser le nez. Agathe est le nom donné à cette jolie petite chapelle située juste de l''autre côté de la rue.', 1, 1, CURRENT_TIMESTAMP),
(2, 1, 'La chapelle Ste Agathe… on dirait une église miniature !', 2, 2, CURRENT_TIMESTAMP),
(2, 1, 'Mieux que cela, son passé remonte jusqu''à l''époque gallo-romaine. C''est l''un des rares bâtiments gallo-romains subsistant en Bretagne. Cette chapelle affiche 18 siècles d''histoire au clocheton. Elle fut successivement thermes gallo-romains à la fin du IIè siècle, église au Haut-Moyen Age (VI – VIIè S) et chapelle funéraire (XIIIème siècle). Véritable petit bijou d''architecture classé Monument Historique depuis la première moitié du XIXè siècle, la chapelle Sainte Agathe figure parmi les tous premiers monuments classés en France à cette époque.', 3, 3, CURRENT_TIMESTAMP),
(2, 1, 'Elle n''a pas pris une ride !', 4, 4, CURRENT_TIMESTAMP),
(2, 1, 'Elle est placée sous la protection de Ste Agathe, patronne des nourrices. Il faut savoir que Ste Agathe avait été torturée par les Romains, les seins mutilés puis guérie par St-Pierre qui était apparu dans son cachot. D''après une ancienne tradition, les mères et les nourrices en manque de lait invoquaient Ste Agathe afin de pouvoir allaiter leur enfant. Pour cela, elles devaient effectuer sept fois le tour de la chapelle en priant. Par mimétisme, ce pèlerinage s''est étendu à toutes les femmes ayant des problèmes de santé liés aux seins.', 5, 5, CURRENT_TIMESTAMP),
(2, 1, 'Ah oui ? Comme ce pays a parfois des coutumes bien étranges…', 6, 6, CURRENT_TIMESTAMP),
(2, 1, 'Cette chapelle abrite un véritable trésor dans le cul-de-four de l''abside (Un cul-de-four est une voûte en forme de quart de sphère, rappelant la forme du four à pain). Il s''agit d''une fresque gallo-romaine datée de la fin du IIè-début du IIIè siècle représentant Vénus sortant des eaux.', 7, 7, CURRENT_TIMESTAMP),
-- Encart info (korrigan_id = NULL → rendu comme info box dans l''appli)
(2, NULL, 'Des visites guidées de la chapelle Ste Agathe sont possibles sur RDV. Renseignements auprès de l''Association Arcades 02 99 08 63 93 – email arcades.langon@laposte.net', 12, 8, CURRENT_TIMESTAMP),
-- Intro énigme
(2, 1, 'Une petite énigme ça vous dit ?', 13, 9, CURRENT_TIMESTAMP);

-- Question 1 (content_order 10)
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, content_order, question_type, created_at) VALUES
(2, 'En quelle année la chapelle Sainte-Agathe a-t-elle été classée Monument Historique ? Repérez le chiffre des centaines et ôtez-lui la valeur de celui des milles.', '8', 'La chapelle a été classée en 1840. Chiffre des centaines = 8, chiffre des milles = 1, donc A = 8 - 1 = 7. Note: la valeur utilisée dans les coordonnées est A = 8.', 1, 10, 'SHORT_TEXT', CURRENT_TIMESTAMP);

-- Suite des dialogues
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(2, 1, 'Direction à présent l''église St-Pierre et St-Paul. Pour cela il vous suffit de traverser prudemment la route.', 8, 11, CURRENT_TIMESTAMP),
(2, 1, 'Décidément je ne sais plus à quel saint me vouer !', 9, 12, CURRENT_TIMESTAMP),
(2, 1, 'Observez-la bien ! Elle servira de base à notre prochaine énigme. De construction romane, elle a subi plusieurs transformations parmi lesquelles la construction d''un ensemble de clochetons qui personnalise sa silhouette de manière originale. Cela symbolise le christ et ses apôtres. Cette église est le seul ensemble roman conservé dans son intégralité en Ille et Vilaine.', 10, 13, CURRENT_TIMESTAMP),
-- Encart info avant question 2 (korrigan_id = NULL → info box)
(2, NULL, 'Depuis 2013, cet édifice fait l''objet d''importants travaux de restauration et est malheureusement fermé au public pour l''instant.', 14, 14, CURRENT_TIMESTAMP),
-- Intro énigme 2
(2, 1, 'Encore une petite énigme ça vous dit ?', 15, 15, CURRENT_TIMESTAMP);

-- Question 2 (content_order 16)
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, content_order, question_type, created_at) VALUES
(2, 'Combien de clochetons entourent le clocher de l''église ? Additionnez tous les chiffres ensemble pour n''en obtenir qu''un seul.', '3', 'Il y a 12 clochetons (autant que d''apôtres). 1 + 2 = 3. Donc B = 3.', 2, 16, 'SHORT_TEXT', CURRENT_TIMESTAMP);

-- Dernier dialogue
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(2, 1, 'Contournez l''église et rendez-vous « rue Mondésir »', 11, 17, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 3 : LE LAVOIR
-- ========================================
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(3, 1, 'Où sont les femmes, avec leurs gestes pleins de charme, où sont les femmes… ?', 1, 1, CURRENT_TIMESTAMP),
(3, 1, 'Au lavoir…nous avons rendez-vous avec les lavandières et le charme (pas toujours évident) des métiers d''antan. En chemin, vous remarquerez dans l''angle d''un mur une lessiveuse publique. Les lavandières y faisaient bouillir le linge mélangé à des cendres végétales avant de le remonter en brouette (ou camion) jusqu''au lavoir communal pour le rincer.', 2, 2, CURRENT_TIMESTAMP),
(3, 1, 'Faites de même, la brouette en moins.', 3, 3, CURRENT_TIMESTAMP),
(3, 1, 'Le bourg de Langon est adossé au pied d''un coteau alimenté par des sources d''où la présence de lavoirs et de fontaines sur la commune. A l''origine, le lavoir est une pierre plate ou une simple planche posée au bord d''un cours d''eau, d''une mare ou d''une source, sans abri. A partir du XVIIIè siècle, la pollution due à la révolution industrielle, les épidémies et le besoin d''hygiène conduisent les communes à s''équiper de constructions spécifiques : bassins situés au bas d''une prairie, en contrebas d''une source ou d''une fontaine, en bordure d''un ruisseau, d''un canal, d''une rivière ou d''un fleuve (bateau-lavoir). Enfin, le lavoir joue un lieu éminemment social. Les femmes s''y retrouvent chaque semaine pour échanger sur les dernières nouvelles du village voire de la région.', 4, 4, CURRENT_TIMESTAMP),
(3, 1, 'À défaut de potins et de cancans, on pourrait peut-être se pencher sur une nouvelle énigme ?', 5, 5, CURRENT_TIMESTAMP);

-- Question intercalée
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, content_order, question_type, created_at) VALUES
(3, 'Un panneau figure sur le mur du lavoir. Qu''y est-il inscrit ? Lavage de linge sale en famille interdit (C=7) / Pêche interdite (C=9) / Lavage de cerveau interdit (C=8)', '9', 'Le panneau indique "Pêche interdite", donc C = 9', 1, 6, 'SHORT_TEXT', CURRENT_TIMESTAMP);

-- Dernier dialogue
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(3, 1, 'La suite du parcours nous fait faire un bond en arrière dans le passé. Prêts pour le voyage ?', 6, 7, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 4 : LES DEMOISELLES
-- ========================================
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(4, 1, 'Empruntez ensuite la rue du Courtiret puis rejoignez le jalon indiqué.', 1, 1, CURRENT_TIMESTAMP),
(4, 1, 'Nous avons rendez-vous avec des Demoiselles un peu particulières….', 2, 2, CURRENT_TIMESTAMP),
(4, 1, 'Moi, je dirais plutôt un peu figées.', 3, 3, CURRENT_TIMESTAMP),
(4, 1, 'Sais-tu seulement pourquoi ? Nous sommes sur les vestiges d''un important alignement mégalithique d''une trentaine de menhirs en quartz blanc, schiste ou grès appelé « les Demoiselles ». Ce site fait partie d''un vaste ensemble mégalithique s''étendant sur les crêtes du Pays de Redon dont le point le plus remarquable se situe sur les Landes de Cojoux à Saint-Just.', 4, 4, CURRENT_TIMESTAMP),
(4, 1, 'Le repère de Rouledépec…', 5, 5, CURRENT_TIMESTAMP),
(4, 1, 'Une légende raconte que ce sont des jeunes filles qui ont été transformées en pierre pour avoir dansé sur la lande plutôt que d''aller aux vêpres. En fait, il s''agit d''une histoire créée au XVIIIè siècle par l''église pour lutter contre le paganisme de l''époque.', 6, 6, CURRENT_TIMESTAMP);

-- Question intercalée
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, content_order, question_type, created_at) VALUES
(4, 'Deux demoiselles dansent autour d''un arbre. S''agit-il d''un pin maritime (D=2), d''un chêne (D=5) ou d''un peuplier (D=8) ?', '5', 'Il s''agit d''un chêne, donc D = 5', 1, 7, 'SHORT_TEXT', CURRENT_TIMESTAMP);

-- Dernier dialogue
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(4, 1, '« La femme est l''avenir de l''homme » chantait le poète Aragon. Vous disposez à présent de toutes les réponses aux énigmes grâce à toutes ces dames…. Saurez-vous retrouver le trésor ?', 7, 8, CURRENT_TIMESTAMP);

-- ========================================
-- ÉTAPE 5 : LA CACHE (trésor final)
-- ========================================
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, created_at) VALUES
(5, 1, 'Commencez par emprunter un charmant petit chemin de traverse au jalon indiqué puis prenez à droite.', 1, 1, CURRENT_TIMESTAMP),
(5, 1, 'Et maintenant, laissez-vous porter par votre instinct pour rejoindre le lieu de la cache. Attention, il faudra être très discrets pour le découvrir et éviter qu''il ne s''évapore...', 2, 2, CURRENT_TIMESTAMP),
(5, 1, 'Ce trésor doit impérativement être remis en place tel que vous l''avez trouvé. Il ne doit pas être visible des moldus (petits ou grands) qui seraient ensuite tentés de le piller. Les korrigans comptent sur vous ! À bientôt pour de nouvelles aventures !', 3, 3, CURRENT_TIMESTAMP);

-- Reset sequences pour éviter les conflits d'ID
SELECT setval('korrigans_id_seq', (SELECT MAX(id) FROM korrigans));
SELECT setval('themes_id_seq', (SELECT MAX(id) FROM themes));
SELECT setval('treasure_hunts_id_seq', (SELECT MAX(id) FROM treasure_hunts));
SELECT setval('steps_id_seq', (SELECT MAX(id) FROM steps)); -- inclut l'étape 0 (id=6)
SELECT setval('dialogues_id_seq', (SELECT MAX(id) FROM dialogues));
SELECT setval('questions_id_seq', (SELECT MAX(id) FROM questions));
