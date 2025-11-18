CREATE TABLE time_slot (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) UNIQUE NOT NULL,
                           startTime TIME NOT NULL,
                           endTime TIME NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT valid_time_range CHECK (endTime > startTime),
    CONSTRAINT reasonable_duration CHECK ((endTime - startTime) BETWEEN INTERVAL '1 minute' AND INTERVAL '24 hours'),

);