-- Migration pour ajouter la colonne first_completed_at à la table user_progress
-- À exécuter manuellement sur les bases de données existantes

ALTER TABLE user_progress
ADD COLUMN IF NOT EXISTS first_completed_at TIMESTAMP;

-- Optionnel : Initialiser first_completed_at avec la valeur de completed_at pour les parcours déjà terminés
UPDATE user_progress
SET first_completed_at = completed_at
WHERE is_completed = true AND first_completed_at IS NULL;
