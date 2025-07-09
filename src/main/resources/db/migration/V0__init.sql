CREATE TABLE users
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    nickname      VARCHAR(255),
    email         VARCHAR(255),
    oauth_id      VARCHAR(255),
    provider      ENUM ('KAKAO'),
    role          ENUM ('ADMIN', 'USER') NOT NULL,
    is_withdrawal BIT    NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

-- V1__initialize_schema.sql
CREATE TABLE time_capsule_user
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    user_id    BIGINT    NOT NULL,
    capsule_id BIGINT    NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE time_capsule
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    invite_code CHAR(36)     NOT NULL, -- UUID 형식
    title       VARCHAR(255) NOT NULL,
    subtitle    VARCHAR(255) NOT NULL,
    access_type VARCHAR(255) NOT NULL,
    open_at     TIMESTAMP    NOT NULL,
    closed_at   TIMESTAMP    NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE letter
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    capsule_id    BIGINT       NOT NULL,
    user_id       BIGINT       NOT NULL,
    from_nickname VARCHAR(255) NOT NULL,
    content       TEXT         NOT NULL,
    background    VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE time_capsule_like
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    user_id    BIGINT    NOT NULL,
    capsule_id BIGINT    NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE photo
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    letter_id  BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
