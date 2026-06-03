-- Sample data for testing

-- Insert Korrigans
INSERT INTO korrigans (name, description, image_url) VALUES
    ('Pluzinkopec', 'Le Korrigan du Commerce et des Affaires', 'https://example.com/pluzinkopec.png'),
    ('Epidanl''Bec', 'La Korrigane du Retour à la Nature', 'https://example.com/epidanl-bec.png'),
    ('Cromatik', 'Le Korrigan des Arts et du Savoir-Faire', 'https://example.com/cromatik.png'),
    ('Marin d''Odouss', 'Le Korrigan des Écluses et Canaux', 'https://example.com/marin-odouss.png'),
    ('Panosolec', 'Le Korrigan de l''Écologie', 'https://example.com/panosolec.png'),
    ('Beursalec', 'Le Korrigan de la Gastronomie', 'https://example.com/beursalec.png'),
    ('Queen Aman', 'La Korrigane de l''Histoire', 'https://example.com/queen-aman.png')
ON CONFLICT DO NOTHING;

-- Insert Themes
INSERT INTO themes (name, description, image_url, korrigan_id) VALUES
    ('Commerce', 'Découvrez les secrets du commerce en Haute Bretagne', 'https://example.com/theme-commerce.png', 1),
    ('Retour à la Nature', 'Explorez la nature et les traditions rurales', 'https://example.com/theme-nature.png', 2),
    ('Arts et Savoir-Faire', 'Les techniques et arts traditionnels bretons', 'https://example.com/theme-arts.png', 3),
    ('Écluses et Canaux', 'L''histoire des voies navigables de Bretagne', 'https://example.com/theme-canaux.png', 4),
    ('Écologie', 'La protection de l''environnement breton', 'https://example.com/theme-ecologie.png', 5),
    ('Gastronomie', 'Les spécialités culinaires de Haute Bretagne', 'https://example.com/theme-gastronomie.png', 6),
    ('Histoire', 'Les grands événements de l''histoire bretonne', 'https://example.com/theme-histoire.png', 7)
ON CONFLICT DO NOTHING;

-- Insert Treasure Hunts (one per theme)
INSERT INTO treasure_hunts (title, description, theme_id, final_latitude, final_longitude, treasure_image_url, is_active) VALUES
    ('La Route des Marchands', 'Suivez les pas des anciens marchands bretons', 1, 48.1234, -1.5678, 'https://example.com/treasure-commerce.png', true),
    ('Les Chemins Verts', 'Parcourez les sentiers de la Haute Bretagne', 2, 48.2234, -1.4678, 'https://example.com/treasure-nature.png', true),
    ('L''Atelier Oublié', 'Retrouvez les secrets des maîtres artisans', 3, 48.3234, -1.3678, 'https://example.com/treasure-arts.png', true),
    ('Les Écluses du Temps', 'Découvrez l''histoire des voies d''eau', 4, 48.4234, -1.2678, 'https://example.com/treasure-canaux.png', true),
    ('La Forêt Protégée', 'Explorez la nature sauvage préservée', 5, 48.5234, -1.1678, 'https://example.com/treasure-ecologie.png', true),
    ('Les Saveurs Cachées', 'Traque les délices gastronomiques', 6, 48.6234, -1.0678, 'https://example.com/treasure-gastronomie.png', true),
    ('Les Traces du Passé', 'Remontez aux sources de l''histoire bretonne', 7, 48.7234, -0.9678, 'https://example.com/treasure-histoire.png', true)
ON CONFLICT DO NOTHING;

-- Insert Steps (2 steps per treasure hunt)
INSERT INTO steps (treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters) VALUES
    (1, 1, 'La Vieille Boutique', 'Retrouvez la plus ancienne boutique commerçante', 48.1111, -1.5111, 100),
    (1, 2, 'La Place du Marché', 'Le cœur battant du commerce local', 48.1222, -1.5222, 100),
    (2, 1, 'La Clairière Verte', 'Une clairière préservée de la nature', 48.2111, -1.4111, 100),
    (2, 2, 'La Source Cristalline', 'L''eau pure de la Bretagne', 48.2222, -1.4222, 100),
    (3, 1, 'L''Atelier du Potier', 'Le lieu de création des artisans', 48.3111, -1.3111, 100),
    (3, 2, 'La Galerie des Maîtres', 'Les œuvres des grands créateurs', 48.3222, -1.3222, 100),
    (4, 1, 'L''Écluse Principale', 'Le mécanisme ancestral', 48.4111, -1.2111, 150),
    (4, 2, 'Le Chemin de Halage', 'La voie des anciens mariniers', 48.4222, -1.2222, 150),
    (5, 1, 'La Réserve Naturelle', 'Zone protégée avec faune et flore', 48.5111, -1.1111, 100),
    (5, 2, 'L''Observatoire Écologique', 'Point d''observation privilégié', 48.5222, -1.1222, 100),
    (6, 1, 'La Crêperie Traditionnelle', 'Les secrets de la vraie crêpe', 48.6111, -1.0111, 100),
    (6, 2, 'Le Marché aux Fruits de Mer', 'Les délices de la côte', 48.6222, -1.0222, 100),
    (7, 1, 'Le Château Historique', 'Forteresse du Moyen Âge', 48.7111, -0.9111, 200),
    (7, 2, 'La Chapelle Ancestrale', 'Sanctuaire des générations', 48.7222, -0.9222, 150)
ON CONFLICT DO NOTHING;

-- Insert Dialogues for all steps
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, audio_url) VALUES
    (1, 1, 'Bienvenue marchand! Tu as trouvé la vieille boutique. Es-tu prêt pour l''épreuve commerciale?', 1, NULL),
    (1, 1, 'Réponds correctement et tu progresseras dans cette quête!', 2, NULL),
    (2, 1, 'Bravo! Te voilà à la Place du Marché, le cœur de nos échanges.', 1, NULL),
    (3, 2, 'Ah, te voilà dans la Clairière Verte! La nature t''accueille.', 1, NULL),
    (3, 2, 'Écoute les sons de la forêt et réponds à ma question...', 2, NULL),
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
    (14, 7, 'La Chapelle Ancestrale est le berceau de notre histoire. Écoute le passé...', 1, NULL)
ON CONFLICT DO NOTHING;

-- Insert Questions for all steps
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, question_type) VALUES
    (1, 'En quelle année cette boutique a-t-elle ouvert?', '1850', 'La vieille boutique date du milieu du XIXe siècle!', 1, 'SHORT_TEXT'),
    (1, 'Quel type de marchandises y était vendu?', 'tissu', 'C''était une boutique de tissus et de draperie!', 2, 'SHORT_TEXT'),
    (2, 'Combien de jours par semaine le marché se tenait-il?', '3', 'Le marché avait lieu trois jours par semaine!', 1, 'SHORT_TEXT'),
    (3, 'Quel animal symbolise cette forêt?', 'cerf', 'Le cerf est le roi de cette forêt enchantée!', 1, 'SHORT_TEXT'),
    (3, 'Combien d''arbres centenaires compte cette clairière?', '12', 'Douze arbres majestueux peuplent ce lieu!', 2, 'SHORT_TEXT'),
    (4, 'Quelle est la profondeur de la source?', '5', 'La source a une profondeur de 5 mètres!', 1, 'SHORT_TEXT'),
    (5, 'Quel est le tour de mains du potier?', 'tournage', 'Le tournage sur roue est son art principal!', 1, 'SHORT_TEXT'),
    (5, 'En combien de temps se cuit la céramique?', '2', 'Deux heures sont nécessaires pour cuire une pièce!', 2, 'SHORT_TEXT'),
    (6, 'Combien de maîtres ont signé les œuvres?', '7', 'Sept grands maîtres ont créé ces chefs-d''œuvre!', 1, 'SHORT_TEXT'),
    (7, 'En quelle année l''écluse a-t-elle été construite?', '1750', 'L''écluse date de 1750!', 1, 'SHORT_TEXT'),
    (7, 'Quelle était sa profondeur originelle?', '3', 'Une profondeur de 3 mètres permettait la navigation!', 2, 'SHORT_TEXT'),
    (8, 'Quel était le chargement principal?', 'ardoise', 'L''ardoise était le bien le plus transporté!', 1, 'SHORT_TEXT'),
    (9, 'Combien d''espèces protégées vivent ici?', '47', 'La réserve abrite 47 espèces protégées!', 1, 'SHORT_TEXT'),
    (9, 'Quel oiseau rare peut-on observer?', 'aigles', 'Les aigles pêcheurs habitent ce sanctuaire!', 2, 'SHORT_TEXT'),
    (10, 'Quelle est la faune principale observée?', 'renards', 'Les renards et chevreuils sont les habitants principaux!', 1, 'SHORT_TEXT'),
    (11, 'Combien d''ingrédients traditionnels pour une crêpe?', '4', 'Farine, lait, œuf et beurre: les quatre piliers!', 1, 'SHORT_TEXT'),
    (11, 'Quel est le temps de cuisson classique?', '2', 'Deux minutes pour une crêpe parfaite!', 2, 'SHORT_TEXT'),
    (12, 'Quel fruit de mer est le plus savoureux?', 'huître', 'L''huître est la reine de nos côtes!', 1, 'SHORT_TEXT'),
    (13, 'En quelle année le château a-t-il été construit?', '1350', 'Le château remonte à 1350!', 1, 'SHORT_TEXT'),
    (13, 'Combien de tours de défense possède-t-il?', '5', 'Cinq tours impressionnantes le défendaient!', 2, 'SHORT_TEXT'),
    (14, 'En quelle année la chapelle a-t-elle été fondée?', '1200', 'La chapelle fut fondée au XIIIe siècle!', 1, 'SHORT_TEXT'),
    (14, 'Quel saint est vénéré dans cette chapelle?', 'saint yves', 'Saint Yves est le patron des juristes et des justes!', 2, 'SHORT_TEXT')
ON CONFLICT DO NOTHING;

-- Add more test users if needed
-- Users will be created through the signup API
