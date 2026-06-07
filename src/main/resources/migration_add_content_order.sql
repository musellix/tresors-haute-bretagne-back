-- Migration: Add content_order to dialogues and questions
-- This allows dialogues and questions to be interleaved in a single sequence

-- Add content_order column to dialogues
ALTER TABLE dialogues
ADD COLUMN IF NOT EXISTS content_order INTEGER;

-- Add content_order column to questions
ALTER TABLE questions
ADD COLUMN IF NOT EXISTS content_order INTEGER;

-- Initialize content_order for existing dialogues
-- Dialogues keep their dialogue_order as content_order initially
UPDATE dialogues
SET content_order = dialogue_order
WHERE content_order IS NULL;

-- Initialize content_order for existing questions
-- Questions are placed after all dialogues in each step
UPDATE questions q
SET content_order = (
    SELECT COALESCE(MAX(d.dialogue_order), 0) + q.question_order
    FROM dialogues d
    WHERE d.step_id = q.step_id
)
WHERE q.content_order IS NULL;

-- Make content_order NOT NULL after initialization
ALTER TABLE dialogues
ALTER COLUMN content_order SET NOT NULL;

ALTER TABLE questions
ALTER COLUMN content_order SET NOT NULL;
