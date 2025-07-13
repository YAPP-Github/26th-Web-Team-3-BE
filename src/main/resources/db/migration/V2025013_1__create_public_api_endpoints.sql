CREATE TABLE api_auth
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    path       VARCHAR(255)                  NOT NULL,
    method     VARCHAR(10)                   NOT NULL,
    auth_type  ENUM ('REQUIRED', 'OPTIONAL', 'NONE') NOT NULL,
    created_at DATETIME(6)                   NOT NULL,
    updated_at DATETIME(6)                   NOT NULL
);
