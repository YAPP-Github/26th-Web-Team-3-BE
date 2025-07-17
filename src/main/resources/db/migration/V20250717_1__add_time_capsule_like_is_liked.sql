ALTER TABLE time_capsule_like
    ADD COLUMN is_liked BOOLEAN NOT NULL DEFAULT TRUE AFTER capsule_id;
