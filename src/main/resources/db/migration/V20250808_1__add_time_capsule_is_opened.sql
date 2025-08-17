ALTER TABLE time_capsule_user
    ADD COLUMN is_opened   tinyint(1) NOT NULL DEFAULT 0;
