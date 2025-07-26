ALTER TABLE time_capsule
    ADD COLUMN creator_id BIGINT NOT NULL UNIQUE,
    ADD CONSTRAINT fk_time_capsule_creator
    FOREIGN KEY (creator_id) REFERENCES users(id);
