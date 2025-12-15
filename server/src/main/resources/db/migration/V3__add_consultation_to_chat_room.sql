-- Migration script to add consultation_id column to chat_room table
-- This links chat rooms to specific consultations

-- Add the consultation_id column
ALTER TABLE chat_room 
ADD COLUMN consultation_id BIGINT;

-- Add foreign key constraint
ALTER TABLE chat_room
ADD CONSTRAINT fk_chat_room_consultation 
FOREIGN KEY (consultation_id) 
REFERENCES consultation(id)
ON DELETE CASCADE;

-- Create index for better query performance
CREATE INDEX idx_chat_room_consultation_id ON chat_room(consultation_id);

-- Optional: Add unique constraint to ensure one chat room per consultation
-- ALTER TABLE chat_room ADD CONSTRAINT uk_chat_room_consultation UNIQUE (consultation_id);

COMMIT;
