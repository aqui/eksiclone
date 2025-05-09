CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME,
    last_updated_date DATETIME,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    version BIGINT
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME,
    last_updated_date DATETIME,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    version BIGINT
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Varsayılan roller
INSERT INTO roles (created_date, last_updated_date, role_name, version)
VALUES 
    (NOW(), NOW(), 'ROLE_ADMIN', 0),
    (NOW(), NOW(), 'ROLE_MODERATOR', 0),
    (NOW(), NOW(), 'ROLE_USER', 0);

-- Varsayılan kullanıcılar (Güvenlik amaçlı farklı şifreler)
-- Not: Şifreler BCrypt ile hashlenmiştir. Tüm kullanıcılar için orijinal şifre: "password123"
INSERT INTO users (created_date, last_updated_date, username, email, password, name, last_name, version)
VALUES 
    (NOW(), NOW(), 'admin_user', 'admin@example.com', '$2a$12$oZV.nkCo2MLCU8DgSzvnaeXHMwKj5m2o4SRCZ/2afPBrCMPrvfE4W', 'Admin', 'User', 0),
    (NOW(), NOW(), 'moderator_user', 'moderator@example.com', '$2a$12$oZV.nkCo2MLCU8DgSzvnaeXHMwKj5m2o4SRCZ/2afPBrCMPrvfE4W', 'Moderator', 'User', 0),
    (NOW(), NOW(), 'normal_user', 'user@example.com', '$2a$12$oZV.nkCo2MLCU8DgSzvnaeXHMwKj5m2o4SRCZ/2afPBrCMPrvfE4W', 'Normal', 'User', 0);

-- Kullanıcı-rol ilişkileri
INSERT INTO user_role (user_id, role_id)
VALUES 
    (1, 1), -- admin_user -> ROLE_ADMIN
    (2, 2), -- moderator_user -> ROLE_MODERATOR
    (3, 3); -- normal_user -> ROLE_USER