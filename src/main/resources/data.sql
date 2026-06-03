-- Sample data for testing

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

-- Insert Treasure Hunts (11 regular + 2 quest hunts)
INSERT INTO treasure_hunts (title, description, theme_id, final_latitude, final_longitude, treasure_image_url, is_active) VALUES
    ('La Route des Marchands', 'Suivez les pas des anciens marchands bretons', 1, 48.1234, -1.5678, 'https://example.com/treasure-commerce.png', true),
    ('Les Chemins Verts', 'Parcourez les sentiers de la Haute Bretagne', 2, 48.2234, -1.4678, 'https://example.com/treasure-nature.png', true),
    ('L''Atelier Oublié', 'Retrouvez les secrets des maîtres artisans', 3, 48.3234, -1.3678, 'https://example.com/treasure-arts.png', true),
    ('Les Écluses du Temps', 'Découvrez l''histoire des voies d''eau', 4, 48.4234, -1.2678, 'https://example.com/treasure-canaux.png', true),
    ('La Forêt Protégée', 'Explorez la nature sauvage préservée', 5, 48.5234, -1.1678, 'https://example.com/treasure-ecologie.png', true),
    ('Les Saveurs Cachées', 'Traque les délices gastronomiques', 6, 48.6234, -1.0678, 'https://example.com/treasure-gastronomie.png', true),
    ('Les Traces du Passé', 'Remontez aux sources de l''histoire bretonne', 7, 48.7234, -0.9678, 'https://example.com/treasure-histoire.png', true),
    ('L''Appel des Vagues', 'Découvrez les secrets de la côte atlantique', 8, 48.8234, -0.8678, 'https://example.com/treasure-mer.png', true),
    ('Cité Urbaine', 'Les richesses du patrimoine citadin', 9, 48.9234, -0.7678, 'https://example.com/treasure-urbain.png', true),
    ('L''Aventure en Mouvement', 'Défi sportif et découverte', 10, 47.0234, -1.6678, 'https://example.com/treasure-sport.png', true),
    ('Secrets de Pierre', 'Les énigmes des mégalithes anciens', 11, 47.1234, -1.5678, 'https://example.com/treasure-megalithe.png', true),
    ('Quête de Darkann', 'Mission spéciale roublarde', 12, 47.2234, -1.4678, 'https://example.com/treasure-darkann.png', true),
    ('Monde de Korry Gan', 'Quête finale généraliste', 13, 47.3234, -1.3678, 'https://example.com/treasure-korrygan.png', true)
ON CONFLICT DO NOTHING;

-- Insert Steps (2 steps per treasure hunt, 26 total)
INSERT INTO steps (treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters) VALUES
    -- Hunt 1: Commerce
    (1, 1, 'La Vieille Boutique', 'Retrouvez la plus ancienne boutique commerçante', 48.1111, -1.5111, 100),
    (1, 2, 'La Place du Marché', 'Le cœur battant du commerce local', 48.1222, -1.5222, 100),
    -- Hunt 2: Nature
    (2, 1, 'La Clairière Verte', 'Une clairière préservée de la nature', 48.2111, -1.4111, 100),
    (2, 2, 'La Source Cristalline', 'L''eau pure de la Bretagne', 48.2222, -1.4222, 100),
    -- Hunt 3: Arts
    (3, 1, 'L''Atelier du Potier', 'Le lieu de création des artisans', 48.3111, -1.3111, 100),
    (3, 2, 'La Galerie des Maîtres', 'Les œuvres des grands créateurs', 48.3222, -1.3222, 100),
    -- Hunt 4: Canaux
    (4, 1, 'L''Écluse Principale', 'Le mécanisme ancestral', 48.4111, -1.2111, 150),
    (4, 2, 'Le Chemin de Halage', 'La voie des anciens mariniers', 48.4222, -1.2222, 150),
    -- Hunt 5: Écologie
    (5, 1, 'La Réserve Naturelle', 'Zone protégée avec faune et flore', 48.5111, -1.1111, 100),
    (5, 2, 'L''Observatoire Écologique', 'Point d''observation privilégié', 48.5222, -1.1222, 100),
    -- Hunt 6: Gastronomie
    (6, 1, 'La Crêperie Traditionnelle', 'Les secrets de la vraie crêpe', 48.6111, -1.0111, 100),
    (6, 2, 'Le Marché aux Fruits de Mer', 'Les délices de la côte', 48.6222, -1.0222, 100),
    -- Hunt 7: Histoire
    (7, 1, 'Le Château Historique', 'Forteresse du Moyen Âge', 48.7111, -0.9111, 200),
    (7, 2, 'La Chapelle Ancestrale', 'Sanctuaire des générations', 48.7222, -0.9222, 150),
    -- Hunt 8: Mer
    (8, 1, 'La Pointe du Grouin', 'Panorama côtier spectaculaire', 48.8111, -0.8111, 150),
    (8, 2, 'Le Port de Cancale', 'Les huîtres et la vie maritime', 48.8222, -0.8222, 100),
    -- Hunt 9: Urbain
    (9, 1, 'Les Rues Anciennes', 'Patrimoine urbain médiéval', 48.9111, -0.7111, 100),
    (9, 2, 'La Gargouille Mystérieuse', 'Secret architectural', 48.9222, -0.7222, 100),
    -- Hunt 10: Sport
    (10, 1, 'La Digue du Sillon', 'Parcours côtier sportif', 47.0111, -1.6111, 150),
    (10, 2, 'Le Sentier de l''Aventure', 'Défi physique bucolique', 47.0222, -1.6222, 100),
    -- Hunt 11: Mégalithe
    (11, 1, 'Le Menhir Principal', 'Monument de pierre ancestral', 47.1111, -1.5111, 200),
    (11, 2, 'Le Dolmen Sacré', 'Tombeau des anciens', 47.1222, -1.5222, 200),
    -- Hunt 12: Darkann (Quête)
    (12, 1, 'Énigme 1 de Darkann', 'Première épreuve roublarde', 47.2111, -1.4111, 150),
    (12, 2, 'Énigme 2 de Darkann', 'Deuxième épreuve complexe', 47.2222, -1.4222, 150),
    -- Hunt 13: Korry Gan (Quête)
    (13, 1, 'Convergence Finale', 'Rassemblement des pouvoirs', 47.3111, -1.3111, 150),
    (13, 2, 'Le Secret des Korrigans', 'Révélation ultime', 47.3222, -1.3222, 150)
ON CONFLICT DO NOTHING;

-- Insert Dialogues for all steps
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, audio_url) VALUES
    (1, 1, 'Bienvenue marchand! Tu as trouvé la vieille boutique. Es-tu prêt pour l''épreuve commerciale?', 1, NULL),
    (2, 1, 'Bravo! Te voilà à la Place du Marché, le cœur de nos échanges.', 1, NULL),
    (3, 2, 'Ah, te voilà dans la Clairière Verte! La nature t''accueille.', 1, NULL),
    (4, 2, 'La Source Cristalline brille! Son eau pure recèle un secret...', 1, NULL),
    (5, 3, 'Bienvenue à l''Atelier du Potier! Observer bien les techniques.', 1, NULL),
    (6, 3, 'Les Maîtres ont créé de magnifiques œuvres. Peux-tu les reconnaître?', 1, NULL),
    (7, 4, 'Voici l''Écluse Principale! Un mécanisme séculaire t''attend.', 1, NULL),
    (8, 4, 'Les mariniers ont navigué par ces chemins. Connais-tu leur histoire?', 1, NULL),
    (9, 5, 'Bienvenue dans la Réserve Naturelle! Observe la faune et la flore.', 1, NULL),
    (10, 5, 'L''Observatoire Écologique révèle les secrets de la nature.', 1, NULL),
    (11, 6, 'Ah, un gourmet! La Crêperie Traditionnelle t''attend.', 1, NULL),
    (12, 6, 'Les fruits de mer de nos côtes sont savoureux. Connais-tu leurs noms?', 1, NULL),
    (13, 7, 'Bienvenue au Château Historique! Des générations l''ont habité.', 1, NULL),
    (14, 7, 'La Chapelle Ancestrale est le berceau de notre histoire. Écoute le passé...', 1, NULL),
    (15, 8, 'Ah, te voilà à la Pointe du Grouin! Le spectacle des vagues t''impressionne?', 1, NULL),
    (16, 8, 'Cancale, capitale mondiale des huîtres! Sauras-tu résoudre mes énigmes?', 1, NULL),
    (17, 9, 'Bienvenue dans les rues anciennes du patrimoine urbain!', 1, NULL),
    (18, 9, 'La Gargouille Mystérieuse guarde ses secrets...', 1, NULL),
    (19, 10, 'Bienvenue sur la Digue du Sillon! Êtes-vous prêts pour l''aventure?', 1, NULL),
    (20, 10, 'Le Sentier de l''Aventure teste vos forces et votre courage!', 1, NULL),
    (21, 11, 'Voici le Menhir Principal! Monolithe des anciens temps!', 1, NULL),
    (22, 11, 'Le Dolmen Sacré renferme les mystères du passé...', 1, NULL),
    (23, 12, 'Je suis Darkann! Acceptes-tu cette première énigme roublarde?', 1, NULL),
    (24, 12, 'Deuxième épreuve : as-tu l''esprit assez vif pour me suivre?', 1, NULL),
    (25, 13, 'Korry Gan t''accueille! Tous les chemins convergent ici!', 1, NULL),
    (26, 13, 'Tu as traversé tous les mondes! Découvre maintenant le secret des Korrigans!', 1, NULL)
ON CONFLICT DO NOTHING;

-- Insert Questions for all steps (2 questions per step)
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, question_type) VALUES
    (1, 'En quelle année cette boutique a-t-elle ouvert?', '1850', 'La vieille boutique date du milieu du XIXe siècle!', 1, 'SHORT_TEXT'),
    (1, 'Quel type de marchandises y était vendu?', '3', 'Tissu, draperie et articles textiles!', 2, 'SHORT_TEXT'),
    (2, 'Combien de jours par semaine le marché se tenait-il?', '3', 'Le marché avait lieu trois jours par semaine!', 1, 'SHORT_TEXT'),
    (2, 'Quel était le jour principal du marché?', '5', 'Vendredi, jour des grand marchés!', 2, 'SHORT_TEXT'),
    (3, 'Quel animal symbolise cette forêt?', '1', 'Le cerf est le roi de cette forêt enchantée!', 1, 'SHORT_TEXT'),
    (3, 'Combien d''arbres centenaires compte cette clairière?', '12', 'Douze arbres majestueux peuplent ce lieu!', 2, 'SHORT_TEXT'),
    (4, 'Quelle est la profondeur de la source?', '5', 'La source a une profondeur de 5 mètres!', 1, 'SHORT_TEXT'),
    (4, 'Quelle est sa température moyenne?', '8', '8 degrés, eau très froide!', 2, 'SHORT_TEXT'),
    (5, 'Quel est le tour de mains du potier?', '7', 'Le tournage sur roue est son art principal!', 1, 'SHORT_TEXT'),
    (5, 'En combien de temps se cuit la céramique?', '2', 'Deux heures sont nécessaires pour cuire une pièce!', 2, 'SHORT_TEXT'),
    (6, 'Combien de maîtres ont signé les œuvres?', '7', 'Sept grands maîtres ont créé ces chefs-d''œuvre!', 1, 'SHORT_TEXT'),
    (6, 'Quelle technique prédominait?', '4', 'La vitrification et l''émaillage!', 2, 'SHORT_TEXT'),
    (7, 'En quelle année l''écluse a-t-elle été construite?', '1750', 'L''écluse date de 1750!', 1, 'SHORT_TEXT'),
    (7, 'Quelle était sa profondeur originelle?', '3', 'Une profondeur de 3 mètres permettait la navigation!', 2, 'SHORT_TEXT'),
    (8, 'Quel était le chargement principal?', '2', 'L''ardoise était le bien le plus transporté!', 1, 'SHORT_TEXT'),
    (8, 'Combien de chevaux tiraient les barges?', '6', 'Six chevaux robustes pour chaque barge!', 2, 'SHORT_TEXT'),
    (9, 'Combien d''espèces protégées vivent ici?', '47', 'La réserve abrite 47 espèces protégées!', 1, 'SHORT_TEXT'),
    (9, 'Quel oiseau rare peut-on observer?', '2', 'Les aigles pêcheurs habitent ce sanctuaire!', 2, 'SHORT_TEXT'),
    (10, 'Quelle est la faune principale observée?', '2', 'Les renards et chevreuils sont les habitants principaux!', 1, 'SHORT_TEXT'),
    (10, 'En quelle année cette réserve a-t-elle été créée?', '1980', 'La réserve date de 1980!', 2, 'SHORT_TEXT'),
    (11, 'Combien d''ingrédients traditionnels pour une crêpe?', '4', 'Farine, lait, œuf et beurre: les quatre piliers!', 1, 'SHORT_TEXT'),
    (11, 'Quel est le temps de cuisson classique?', '2', 'Deux minutes pour une crêpe parfaite!', 2, 'SHORT_TEXT'),
    (12, 'Quel fruit de mer est le plus savoureux?', '1', 'L''huître est la reine de nos côtes!', 1, 'SHORT_TEXT'),
    (12, 'Combien d''huîtres déguste-t-on en une saison?', '9', 'Neuf tonnes de production annuelle!', 2, 'SHORT_TEXT'),
    (13, 'En quelle année le château a-t-il été construit?', '1350', 'Le château remonte à 1350!', 1, 'SHORT_TEXT'),
    (13, 'Combien de tours de défense possède-t-il?', '5', 'Cinq tours impressionnantes le défendaient!', 2, 'SHORT_TEXT'),
    (14, 'En quelle année la chapelle a-t-elle été fondée?', '1200', 'La chapelle fut fondée au XIIIe siècle!', 1, 'SHORT_TEXT'),
    (14, 'Quel saint est vénéré dans cette chapelle?', '1', 'Saint Yves est le patron des justes!', 2, 'SHORT_TEXT'),
    (15, 'Quelle est la hauteur de la Pointe du Grouin?', '40', '40 mètres de falaises majestueuses!', 1, 'SHORT_TEXT'),
    (15, 'Combien d''espèces marines vivent au pied?', '60', '60 espèces marines différentes!', 2, 'SHORT_TEXT'),
    (16, 'Quelle est la production annuelle de Cancale?', '5', '5000 tonnes d''huîtres par an!', 1, 'SHORT_TEXT'),
    (16, 'Depuis combien de siècles cultive-t-on l''huître ici?', '3', 'Trois siècles de tradition!', 2, 'SHORT_TEXT'),
    (17, 'En quelle année la ville a-t-elle été fortifiée?', '1400', 'Fortifications du XVe siècle!', 1, 'SHORT_TEXT'),
    (17, 'Combien de portes d''entrée reste-t-il?', '4', 'Quatre portes historiques subsistent!', 2, 'SHORT_TEXT'),
    (18, 'Combien de gargouilles ornent les bâtiments?', '27', '27 gargouilles symboliques!', 1, 'SHORT_TEXT'),
    (18, 'Quel était leur rôle principal?', '5', 'Protection et évacuation des eaux!', 2, 'SHORT_TEXT'),
    (19, 'Quelle est la longueur de la Digue du Sillon?', '4', '4 kilomètres de marche sportive!', 1, 'SHORT_TEXT'),
    (19, 'Combien de marches d''escaliers à gravir?', '120', '120 marches jusqu''à la crête!', 2, 'SHORT_TEXT'),
    (20, 'Quel est le dénivelé du Sentier de l''Aventure?', '200', '200 mètres de dénivelé positif!', 1, 'SHORT_TEXT'),
    (20, 'Combien de virages périlleux sur ce sentier?', '8', '8 virages requièrent de la prudence!', 2, 'SHORT_TEXT'),
    (21, 'En quelle année ce menhir a-t-il été érigé?', '-2000', 'Érigé en -2000 av. J.-C!', 1, 'SHORT_TEXT'),
    (21, 'Quelle est sa hauteur?', '5', '5 mètres de pierre millénaire!', 2, 'SHORT_TEXT'),
    (22, 'Combien de sépultures contient ce dolmen?', '7', '7 squelettes d''ancêtres!', 1, 'SHORT_TEXT'),
    (22, 'Quel type de pierre le compose?', '1', 'Granit et schiste!', 2, 'SHORT_TEXT'),
    (23, 'Quel nombre apparaît en premier?', '13', 'Le nombre magique 13!', 1, 'SHORT_TEXT'),
    (23, 'Combien d''énigmes avant la révélation?', '7', 'Sept défis roublards!', 2, 'SHORT_TEXT'),
    (24, 'Quel symbole répété indique la voie?', '3', 'Le symbole du triangle!', 1, 'SHORT_TEXT'),
    (24, 'Quel nombre cache se révèle?', '99', 'Le nombre 99!', 2, 'SHORT_TEXT'),
    (25, 'Au centre de tous les mondes, quel nombre?', '1', 'L''unité des Korrigans!', 1, 'SHORT_TEXT'),
    (25, 'Combien de piliers soutiennent ce sanctuaire?', '13', 'Les 13 Korrigans!', 2, 'SHORT_TEXT'),
    (26, 'Quel est le secret ultime?', '1', 'L''amitié et la magie!', 1, 'SHORT_TEXT'),
    (26, 'Que fais-tu maintenant?', '1', 'Tu collecionnes les autres badges!', 2, 'SHORT_TEXT')
ON CONFLICT DO NOTHING;

-- Add more test users if needed
-- Users will be created through the signup API
