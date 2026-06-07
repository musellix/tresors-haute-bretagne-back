-- Sample data

-- Insert Korrigans (13 total)
INSERT INTO korrigans (name, description, image_url) VALUES
    ('Pluzinkopec', 'Le Korrigan du Commerce et des Affaires', 'https://example.com/pluzinkopec.png'),
    ('Epidanl''Bec', 'La Korrigane du Retour à la Nature', 'https://example.com/epidanl-bec.png'),
    ('Cromatik', 'Le Korrigan des Arts et du Savoir-Faire', 'https://example.com/cromatik.png'),
    ('Marin d''Odouss', 'Le Korrigan des Écluses et Canaux', 'https://example.com/marin-odouss.png'),
    ('Panosolec', 'Le Korrigan de l''Écologie', 'https://example.com/panosolec.png'),
    ('Beursalec', 'Le Korrigan de la Gastronomie', 'https://example.com/beursalec.png'),
    ('Queen Aman', 'La Korrigane de l''Histoire', 'https://example.com/queen-aman.png'),
    ('Captain O''ssec', 'Le Korrigan de la Mer et des Littoraux', 'https://example.com/captain-ossec.png'),
    ('Barbobec', 'Le Korrigan des Villes et du Patrimoine Urbain', 'https://example.com/barbobec.png'),
    ('Kronomec', 'Le Korrigan du Sport et de l''Aventure', 'https://example.com/kronomec.png'),
    ('Rouledépecs', 'Le Korrigan des Menhirs et Dolmens', 'https://example.com/rouledepecs.png'),
    ('Darkann', 'Le Korrigan Roublard aux Quêtes Mystérieuses', 'https://example.com/darkann.png'),
    ('Korry Gan', 'Le Korrigan Généraliste et Bienveillant', 'https://example.com/korry-gan.png')
ON CONFLICT DO NOTHING;

-- Insert Themes (13 total)
INSERT INTO themes (name, description, image_url, korrigan_id) VALUES
    ('Commerce', 'Découvrez les secrets du commerce en Haute Bretagne', 'https://example.com/theme-commerce.png', 1),
    ('Retour à la Nature', 'Explorez la nature et les traditions rurales', 'https://example.com/theme-nature.png', 2),
    ('Arts et Savoir-Faire', 'Les techniques et arts traditionnels bretons', 'https://example.com/theme-arts.png', 3),
    ('Écluses et Canaux', 'L''histoire des voies navigables de Bretagne', 'https://example.com/theme-canaux.png', 4),
    ('Écologie', 'La protection de l''environnement breton', 'https://example.com/theme-ecologie.png', 5),
    ('Gastronomie', 'Les spécialités culinaires de Haute Bretagne', 'https://example.com/theme-gastronomie.png', 6),
    ('Histoire', 'Les grands événements de l''histoire bretonne', 'https://example.com/theme-histoire.png', 7),
    ('La Mer', 'Les richesses et légendes du littoral breton', 'https://example.com/theme-mer.png', 8),
    ('Urbain', 'La vie dans les villes de Haute Bretagne', 'https://example.com/theme-urbain.png', 9),
    ('Sport', 'L''aventure et les activités sportives bretonnes', 'https://example.com/theme-sport.png', 10),
    ('Menhirs et Dolmens', 'Les monuments mégalithiques mystérieux', 'https://example.com/theme-megalithe.png', 11),
    ('Quêtes Spéciales', 'Missions secrets pour les plus courageux', 'https://example.com/theme-quete.png', 12),
    ('Le Monde des Korrigans', 'L''univers complet des créatures magiques', 'https://example.com/theme-korrigans.png', 13)
ON CONFLICT DO NOTHING;

-- =============================================================
-- Chasse au trésor réelle : « Au bonheur des dames » (Langon)
-- Thème : Histoire (theme_id 7) - Korrigans : Queen Aman (7) & Korry Gan (13)
-- Type : multicache - Difficulté 2/5 - Durée 1 h
-- =============================================================

-- Insert Treasure Hunt (1)
INSERT INTO treasure_hunts (title, description, theme_id, final_latitude, final_longitude, treasure_image_url, coordinate_formula, is_active) VALUES
    ('Au bonheur des dames', 'Direction Langon, au sud de l''Ille-et-Vilaine en Pays de Redon, à la rencontre des femmes qui ont marqué l''histoire de ce charmant bourg labellisé Commune du Patrimoine Rural de Bretagne. Une multicache (difficulté 2/5, environ 1 h) sur les traces d''Agathe, des lavandières et des Demoiselles de pierre.', 7, 47.720933, -1.849667, NULL, 'N 47°4(B).2(D)(Bx2)'' / W 1°(D)0.(C)(A+1)0''', true)
ON CONFLICT DO NOTHING;

-- Insert Steps (5)
INSERT INTO steps (treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters) VALUES
    (1, 1, 'Départ', 'Garez-vous sur le petit parking près de l''église : il vous permettra de rejoindre facilement votre véhicule à la fin du circuit. C''est le point de départ du parcours.', 47.720050, -1.848883, 40),
    (1, 2, 'Agathe, Pierre et Paul', 'La chapelle gallo-romaine Sainte-Agathe et, juste de l''autre côté de la rue, l''église romane Saint-Pierre-et-Saint-Paul.', 47.720400, -1.848933, 40),
    (1, 3, 'Le lavoir', 'Le lavoir communal, rue Mondésir, où se retrouvaient autrefois les lavandières de Langon.', 47.718983, -1.849483, 30),
    (1, 4, 'Les Demoiselles', 'Les vestiges d''un alignement mégalithique d''une trentaine de menhirs, surnommé « les Demoiselles ».', 47.720800, -1.854483, 60),
    (1, 5, 'La Cache', 'Empruntez le petit chemin de traverse depuis ce jalon, puis prenez à droite. Le trésor est en bois, près de la mare. Soyez discrets pour le découvrir et remettez-le bien en place !', 47.721650, -1.852283, 30)
ON CONFLICT DO NOTHING;

-- Insert Dialogues (par étape, dans l'ordre du récit)
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, content_order, audio_url) VALUES
    -- Étape 1 : Départ (6 dialogues uniquement)
    (1, 7, 'Bonjour tout le monde ! Pour cette chasse aux trésors, direction Langon, commune située au sud de l''Ille-et-Vilaine en Pays de Redon… Là-bas, pas de château fort ni de bataille à vous raconter, mais plutôt des histoires de femmes à travers des monuments et des sites qui évoquent la vie et le passé de ce charmant petit bourg labellisé Commune du Patrimoine Rural de Bretagne.', 1, 1, NULL),
    (1, 13, 'Un cache féministe peut-être ?', 2, 2, NULL),
    (1, 7, 'Pas forcément, mais bien souvent on oublie le rôle joué par les femmes au cours de l''histoire ou même tout simplement dans la vie quotidienne. Si tu veux en savoir plus, tu n''as qu''à me suivre…', 3, 3, NULL),
    (1, 13, 'Le mieux est de vous garer sur le petit parking près de l''église. Il vous permettra de rejoindre facilement votre véhicule à la fin du circuit. Dis-moi, Queen Aman, quelle est la première femme que nous allons rencontrer ?', 4, 4, NULL),
    (1, 7, 'Elle se prénomme Agathe… et elle sera accompagnée.', 5, 5, NULL),
    (1, 13, 'Ah, zut !', 6, 6, NULL),
    -- Étape 2 : Agathe, Pierre et Paul (11 dialogues + 2 questions intercalées)
    (2, 7, 'Mon cher Korry Gan, ici tu risques fort de te casser le nez. Agathe est le nom donné à cette jolie petite chapelle située juste de l''autre côté de la rue.', 1, 1, NULL),
    (2, 13, 'La chapelle Ste Agathe… on dirait une église miniature !', 2, 2, NULL),
    (2, 7, 'Mieux que cela, son passé remonte jusqu''à l''époque gallo-romaine. C''est l''un des rares bâtiments gallo-romains subsistant en Bretagne. Cette chapelle affiche 18 siècles d''histoire au clocheton. Elle fut successivement thermes gallo-romains à la fin du IIè siècle, église au Haut-Moyen Âge (VI–VIIè s.) et chapelle funéraire (XIIIème siècle). Véritable petit bijou d''architecture classé Monument Historique depuis la première moitié du XIXè siècle, la chapelle Sainte-Agathe figure parmi les tous premiers monuments classés en France à cette époque.', 3, 3, NULL),
    (2, 13, 'Elle n''a pas pris une ride !', 4, 4, NULL),
    (2, 7, 'Elle est placée sous la protection de Ste Agathe, patronne des nourrices. Il faut savoir que Ste Agathe avait été torturée par les Romains, les seins mutilés, puis guérie par St-Pierre qui était apparu dans son cachot. D''après une ancienne tradition, les mères et les nourrices en manque de lait invoquaient Ste Agathe afin de pouvoir allaiter leur enfant. Pour cela, elles devaient effectuer sept fois le tour de la chapelle en priant. Par mimétisme, ce pèlerinage s''est étendu à toutes les femmes ayant des problèmes de santé liés aux seins.', 5, 5, NULL),
    (2, 13, 'Ah oui ? Comme ce pays a parfois des coutumes bien étranges…', 6, 6, NULL),
    (2, 7, 'Cette chapelle abrite un véritable trésor dans le cul-de-four de l''abside (un cul-de-four est une voûte en forme de quart de sphère, rappelant la forme du four à pain). Il s''agit d''une fresque gallo-romaine datée de la fin du IIè - début du IIIè siècle, représentant Vénus sortant des eaux.', 7, 7, NULL),
    -- Question A intercalée ici (content_order = 8)
    (2, 7, 'Direction à présent l''église St-Pierre et St-Paul. Pour cela, il vous suffit de traverser prudemment la route.', 8, 9, NULL),
    (2, 13, 'Décidément, je ne sais plus à quel saint me vouer !', 9, 10, NULL),
    (2, 7, 'Observez-la bien ! Elle servira de base à notre prochaine énigme. De construction romane, elle a subi plusieurs transformations parmi lesquelles la construction d''un ensemble de clochetons qui personnalise sa silhouette de manière originale. Cela symbolise le Christ et ses apôtres. Cette église est le seul ensemble roman conservé dans son intégralité en Ille-et-Vilaine. Depuis 2013, cet édifice fait l''objet d''importants travaux de restauration et est malheureusement fermé au public pour l''instant.', 10, 11, NULL),
    -- Question B intercalée ici (content_order = 12)
    (2, 13, 'Contournez l''église et rendez-vous « rue Mondésir ».', 11, 13, NULL),
    -- Étape 3 : Le lavoir (6 dialogues + 1 question intercalée)
    (3, 13, 'Où sont les femmes, avec leurs gestes pleins de charme, où sont les femmes… ?', 1, 1, NULL),
    (3, 7, 'Au lavoir… nous avons rendez-vous avec les lavandières et le charme (pas toujours évident) des métiers d''antan. En chemin, vous remarquerez dans l''angle d''un mur une lessiveuse publique. Les lavandières y faisaient bouillir le linge mélangé à des cendres végétales avant de le remonter en brouette (ou camion) jusqu''au lavoir communal pour le rincer.', 2, 2, NULL),
    (3, 13, 'Faites de même, la brouette en moins.', 3, 3, NULL),
    (3, 7, 'Le bourg de Langon est adossé au pied d''un coteau alimenté par des sources, d''où la présence de lavoirs et de fontaines sur la commune. À l''origine, le lavoir est une pierre plate ou une simple planche posée au bord d''un cours d''eau, d''une mare ou d''une source, sans abri. À partir du XVIIIè siècle, la pollution due à la révolution industrielle, les épidémies et le besoin d''hygiène conduisent les communes à s''équiper de constructions spécifiques. Enfin, le lavoir joue un rôle éminemment social : les femmes s''y retrouvent chaque semaine pour échanger sur les dernières nouvelles du village, voire de la région.', 4, 4, NULL),
    (3, 13, 'À défaut de potins et de cancans, on pourrait peut-être se pencher sur une nouvelle énigme ?', 5, 5, NULL),
    -- Question C intercalée ici (content_order = 6)
    (3, 7, 'La suite du parcours nous fait faire un bond en arrière dans le passé. Prêts pour le voyage ?', 6, 7, NULL),
    -- Étape 4 : Les Demoiselles (7 dialogues + 1 question intercalée)
    (4, 13, 'Empruntez ensuite la rue du Courtiret puis rejoignez le jalon (N 47°43.236'' / W 1°51.160'').', 1, 1, NULL),
    (4, 7, 'Nous avons rendez-vous avec des Demoiselles un peu particulières…', 2, 2, NULL),
    (4, 13, 'Moi, je dirais plutôt un peu figées.', 3, 3, NULL),
    (4, 7, 'Sais-tu seulement pourquoi ? Nous sommes sur les vestiges d''un important alignement mégalithique d''une trentaine de menhirs en quartz blanc, schiste ou grès, appelé « les Demoiselles ». Ce site fait partie d''un vaste ensemble mégalithique s''étendant sur les crêtes du Pays de Redon, dont le point le plus remarquable se situe sur les Landes de Cojoux à Saint-Just.', 4, 4, NULL),
    (4, 13, 'Le repère de Rouledépec…', 5, 5, NULL),
    (4, 7, 'Une légende raconte que ce sont des jeunes filles qui ont été transformées en pierre pour avoir dansé sur la lande plutôt que d''aller aux vêpres. En fait, il s''agit d''une histoire créée au XVIIIè siècle par l''église pour lutter contre le paganisme de l''époque.', 6, 6, NULL),
    -- Question D intercalée ici (content_order = 7)
    (4, 7, '« La femme est l''avenir de l''homme », chantait le poète Aragon. Vous disposez à présent de toutes les réponses aux énigmes grâce à toutes ces dames… Saurez-vous retrouver le trésor ?', 7, 8, NULL),
    -- Étape 5 : La Cache (3 dialogues uniquement)
    (5, 13, 'Commencez par emprunter un charmant petit chemin de traverse au jalon (N 47°43.299'' / W 1°51.137''), puis prenez à droite. Nul besoin de détériorer l''environnement, le trésor est forcément là, sous vos yeux. Merci de respecter le site, il est entretenu par la commune de Langon.', 1, 1, NULL),
    (5, 13, 'Et maintenant, laissez-vous porter par votre instinct pour rejoindre le lieu de la cache. Attention, il faudra être très discrets pour le découvrir et éviter qu''il ne s''évapore… Indication : en bois, près de la mare.', 2, 2, NULL),
    (5, 7, 'Ce trésor doit impérativement être remis en place tel que vous l''avez trouvé. Une fois la cache découverte, n''oubliez pas de signaler votre passage dans le logbook (on adore vous lire) et de remettre la cache là où vous l''avez trouvée. À bientôt pour de nouvelles aventures !', 3, 3, NULL)
ON CONFLICT DO NOTHING;

-- Insert Questions (les énigmes du parcours : A, B, C, D) - intercalées dans les dialogues
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, content_order, question_type) VALUES
    -- Étape 2 - Énigme A (chapelle Sainte-Agathe) - après dialogue 7
    (2, 'En quelle année la chapelle Sainte-Agathe a-t-elle été classée Monument Historique ? Repérez le chiffre des centaines et ôtez-lui la valeur de celui des milliers : ce résultat vaut A.', '7', 'La chapelle Sainte-Agathe a été classée en 1840, parmi les tout premiers monuments classés de France. Chiffre des centaines = 8, chiffre des milliers = 1, donc A = 8 − 1 = 7.', 1, 8, 'SHORT_TEXT'),
    -- Étape 2 - Énigme B (église Saint-Pierre-et-Saint-Paul) - après dialogue 10
    (2, 'Combien de clochetons entourent le clocher de l''église Saint-Pierre-et-Saint-Paul ? Additionnez tous les chiffres pour n''en obtenir qu''un seul : ce résultat vaut B. (Indice : il y a autant de clochetons que d''apôtres.)', '3', 'Il y a 12 clochetons, autant que les 12 apôtres. 1 + 2 = 3, donc B = 3.', 2, 12, 'SHORT_TEXT'),
    -- Étape 3 - Énigme C (le lavoir) - après dialogue 5
    (3, 'Un panneau figure sur le mur du lavoir. Qu''y est-il inscrit ? « Lavage de linge sale en famille interdit » → C = 7 ; « Pêche interdite » → C = 9 ; « Lavage de cerveau interdit » → C = 8.', '9', 'Le panneau du lavoir indique « Pêche interdite » : C = 9.', 1, 6, 'SHORT_TEXT'),
    -- Étape 4 - Énigme D (les Demoiselles) - après dialogue 6
    (4, 'Deux demoiselles dansent autour d''un arbre. S''agit-il d''un pin maritime (D = 2), d''un chêne (D = 5) ou d''un peuplier (D = 8) ?', '5', 'L''arbre est un chêne : D = 5.', 1, 7, 'SHORT_TEXT')
ON CONFLICT DO NOTHING;

-- Note : les coordonnées finales de la cache se déduisent des énigmes
-- N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0' avec A=7, B=3, C=9, D=5
-- => N 47°43.256' / W 1°50.980' => 47.720933, -1.849667 (final_latitude / final_longitude)

-- Add more test users if needed
-- Users will be created through the signup API
