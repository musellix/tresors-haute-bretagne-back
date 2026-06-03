# Database Schema - Les Trésors de Haute Bretagne

## Overview

The database uses a relational model with PostgreSQL to manage treasure hunts, korrigans, users, and their progress.

## Entity Diagram

```
Korrigan (1) ──── (N) Theme
           └────── (N) Dialogue

TreasureHunt (1) ──── (N) Step ──── (N) Dialogue
                  └─── (N) Step ──── (N) Question

User (1) ────── (N) UserProgress
      └────── (N) UserAnswer
```

## Tables Description

### korrigans
- **id**: Primary key
- **name**: Unique korrigan name
- **description**: Character description
- **image_url**: Avatar/image URL
- **created_at**: Creation timestamp

### themes
- **id**: Primary key
- **name**: Theme name (unique)
- **description**: Theme description
- **image_url**: Theme image
- **korrigan_id**: Foreign key to korrigans
- **created_at**: Creation timestamp

### treasure_hunts
- **id**: Primary key
- **title**: Hunt title
- **description**: Hunt description
- **theme_id**: Foreign key to themes
- **final_latitude**: GPS latitude of treasure (shown when hunt complete)
- **final_longitude**: GPS longitude of treasure
- **treasure_image_url**: Final treasure image
- **is_active**: Whether hunt is available
- **created_at/updated_at**: Timestamps

### steps
- **id**: Primary key
- **treasure_hunt_id**: Foreign key to treasure_hunts
- **step_order**: Order in the hunt (1, 2, 3...)
- **title**: Step title
- **description**: Step description
- **latitude**: GPS location
- **longitude**: GPS location
- **radius_meters**: Geofence radius (user must be within this distance)
- **created_at/updated_at**: Timestamps

### dialogues
- **id**: Primary key
- **step_id**: Foreign key to steps
- **korrigan_id**: Foreign key to korrigans (who's speaking)
- **text**: Dialogue content
- **dialogue_order**: Order of dialogues in a step
- **audio_url**: Optional voice/audio file
- **created_at/updated_at**: Timestamps

### questions
- **id**: Primary key
- **step_id**: Foreign key to steps
- **question_text**: The question to ask
- **correct_answer**: Expected answer (not sent to frontend)
- **explanation**: Why the answer is correct
- **question_order**: Order of questions in a step
- **question_type**: 'SHORT_TEXT', 'MULTIPLE_CHOICE', etc.
- **created_at/updated_at**: Timestamps

### users
- **id**: Primary key
- **email**: Unique email address
- **password**: Hashed password
- **name**: User's name
- **avatar_url**: Profile picture
- **is_active**: Account active status
- **created_at/updated_at**: Timestamps

### user_progress
- **id**: Primary key
- **user_id**: Foreign key to users
- **treasure_hunt_id**: Foreign key to treasure_hunts
- **current_step**: Current step number (1, 2, 3...)
- **is_completed**: Whether hunt is finished
- **is_treasure_unlocked**: Whether final treasure location is visible
- **started_at**: When user started the hunt
- **completed_at**: When user completed the hunt (null if ongoing)
- **updated_at**: Last update timestamp

### user_answers
- **id**: Primary key
- **user_id**: Foreign key to users
- **question_id**: Foreign key to questions
- **answer**: User's answer text
- **is_correct**: Whether answer was correct
- **answered_at**: When question was answered

## Business Logic

### Unlocking Steps
1. User must be physically near step GPS location (within radius_meters)
2. All questions from previous step must be answered correctly
3. Current step becomes accessible

### Unlocking Treasure
1. User must complete all steps
2. All questions in all steps must be answered correctly
3. Final treasure GPS coordinates (final_latitude, final_longitude) become visible
4. is_treasure_unlocked is set to true

### Progress Tracking
- UserProgress tracks user's journey through a TreasureHunt
- current_step shows which step user is on
- Answers are tracked in UserAnswer table with correctness

## Indexes

Indexes are created for common queries:
- theme lookup by korrigan
- treasure hunt lookup by theme
- step ordering
- dialogue/question ordering
- user progress lookups
- answer history lookups

## Data Initialization

Sample data is loaded from `data.sql` at startup:
- 3 sample Korrigans
- 3 sample Themes (each linked to a Korrigan)
- 2 sample Treasure Hunts
- Steps, dialogues, and questions for testing
