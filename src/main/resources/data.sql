-- Sample data for testing

-- Insert Korrigans
INSERT INTO korrigans (name, description, image_url) VALUES
    ('Gribouille', 'Un korrigan facétieux et malicieux', 'https://example.com/korrigan1.png'),
    ('Fleur-de-bruyère', 'Une korrigane douce et bienveillante', 'https://example.com/korrigan2.png'),
    ('Tonnerre', 'Un korrigan courageux et aventurier', 'https://example.com/korrigan3.png')
ON CONFLICT DO NOTHING;

-- Insert Themes
INSERT INTO themes (name, description, image_url, korrigan_id) VALUES
    ('Les Merveilles de la Forêt', 'Découvrez les secrets cachés de la forêt de Brocéliande', 'https://example.com/theme1.png', 1),
    ('Les Légendes Arthuriennes', 'Sur les traces du Roi Arthur et des Chevaliers de la Table Ronde', 'https://example.com/theme2.png', 2),
    ('Les Trésors Enfouis', 'Les richesses mystérieuses enterrées en Haute Bretagne', 'https://example.com/theme3.png', 3)
ON CONFLICT DO NOTHING;

-- Insert Treasure Hunts
INSERT INTO treasure_hunts (title, description, theme_id, final_latitude, final_longitude, treasure_image_url, is_active) VALUES
    ('Chasse aux Pierres Magiques', 'Retrouvez les trois pierres magiques cachées dans la forêt', 1, 48.1234, -1.5678, 'https://example.com/treasure1.png', true),
    ('La Quête de la Table Ronde', 'Parcourez les lieux légendaires arthurien', 2, 48.2234, -1.4678, 'https://example.com/treasure2.png', true)
ON CONFLICT DO NOTHING;

-- Insert Steps for first treasure hunt
INSERT INTO steps (treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters) VALUES
    (1, 1, 'Le Chêne Ancien', 'Trouvez le grand chêne au cœur de la forêt', 48.1111, -1.5111, 100),
    (1, 2, 'La Source Cristalline', 'Découvrez la source cachée aux eaux pures', 48.1222, -1.5222, 100),
    (1, 3, 'L''Autel des Fées', 'Atteindrez l''endroit sacré des fées', 48.1333, -1.5333, 100)
ON CONFLICT DO NOTHING;

-- Insert Steps for second treasure hunt
INSERT INTO steps (treasure_hunt_id, step_order, title, description, latitude, longitude, radius_meters) VALUES
    (2, 1, 'Le Château de Combourg', 'Début de la quête légendaire', 48.3111, -1.3111, 150),
    (2, 2, 'La Chapelle Merlin', 'Le lieu du grand sage', 48.3222, -1.3222, 150)
ON CONFLICT DO NOTHING;

-- Insert Dialogues for steps
INSERT INTO dialogues (step_id, korrigan_id, text, dialogue_order, audio_url) VALUES
    (1, 1, 'Bienvenue jeune voyageur! Tu es maintenant face au grand chêne millénaire. Es-tu prêt pour l''épreuve?', 1, NULL),
    (1, 1, 'Ce chêne a traversé les âges et garde en lui les secrets de la forêt...', 2, NULL),
    (2, 2, 'Ah, te voilà! La source cristalline attend ton arrivée.', 1, NULL),
    (3, 1, 'L''autel des fées brille d''une lumière mystérieuse...', 1, NULL)
ON CONFLICT DO NOTHING;

-- Insert Questions for steps
INSERT INTO questions (step_id, question_text, correct_answer, explanation, question_order, question_type) VALUES
    (1, 'Combien d''anneaux de croissance peut avoir le grand chêne?', '2000', 'Un grand chêne peut vivre plus de 2000 ans!', 1, 'SHORT_TEXT'),
    (1, 'Quel animal habite dans le chêne?', 'écureuil', 'Les écureuils aiment se faire des réserves de glands!', 2, 'SHORT_TEXT'),
    (2, 'Quelle est la pureté de la source cristalline sur 10?', '10', 'La source est d''une pureté absolue!', 1, 'SHORT_TEXT'),
    (3, 'Comment appelle-t-on les esprits de la nature?', 'fées', 'Les fées sont les esprits gardiens de la nature.', 1, 'SHORT_TEXT')
ON CONFLICT DO NOTHING;

-- Add more test users if needed
-- Users will be created through the signup API
