-- Korrigans table
CREATE TABLE IF NOT EXISTS korrigans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Themes table
CREATE TABLE IF NOT EXISTS themes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    korrigan_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (korrigan_id) REFERENCES korrigans(id)
);

-- Treasure hunts table
CREATE TABLE IF NOT EXISTS treasure_hunts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    theme_id BIGINT NOT NULL,
    final_latitude DOUBLE PRECISION NOT NULL,
    final_longitude DOUBLE PRECISION NOT NULL,
    treasure_image_url VARCHAR(500),
    coordinate_formula VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);

-- Steps table
CREATE TABLE IF NOT EXISTS steps (
    id BIGSERIAL PRIMARY KEY,
    treasure_hunt_id BIGINT NOT NULL,
    step_order INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    radius_meters INTEGER NOT NULL DEFAULT 50,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (treasure_hunt_id) REFERENCES treasure_hunts(id),
    UNIQUE (treasure_hunt_id, step_order)
);

-- Dialogues table
CREATE TABLE IF NOT EXISTS dialogues (
    id BIGSERIAL PRIMARY KEY,
    step_id BIGINT NOT NULL,
    korrigan_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    dialogue_order INTEGER NOT NULL,
    audio_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES steps(id),
    FOREIGN KEY (korrigan_id) REFERENCES korrigans(id)
);

-- Questions table
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    step_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    explanation TEXT,
    question_order INTEGER NOT NULL,
    question_type VARCHAR(50) NOT NULL DEFAULT 'SHORT_TEXT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES steps(id)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    google_id VARCHAR(255) UNIQUE,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- User progress table
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    treasure_hunt_id BIGINT NOT NULL,
    current_step INTEGER NOT NULL DEFAULT 1,
    is_completed BOOLEAN NOT NULL DEFAULT false,
    is_treasure_unlocked BOOLEAN NOT NULL DEFAULT false,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (treasure_hunt_id) REFERENCES treasure_hunts(id),
    UNIQUE (user_id, treasure_hunt_id)
);

-- User answers table
CREATE TABLE IF NOT EXISTS user_answers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Email verification tokens table
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_themes_korrigan ON themes(korrigan_id);
CREATE INDEX IF NOT EXISTS idx_treasure_hunts_theme ON treasure_hunts(theme_id);
CREATE INDEX IF NOT EXISTS idx_steps_hunt ON steps(treasure_hunt_id);
CREATE INDEX IF NOT EXISTS idx_dialogues_step ON dialogues(step_id);
CREATE INDEX IF NOT EXISTS idx_dialogues_korrigan ON dialogues(korrigan_id);
CREATE INDEX IF NOT EXISTS idx_questions_step ON questions(step_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_user ON user_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_hunt ON user_progress(treasure_hunt_id);
CREATE INDEX IF NOT EXISTS idx_user_answers_user ON user_answers(user_id);
CREATE INDEX IF NOT EXISTS idx_user_answers_question ON user_answers(question_id);
