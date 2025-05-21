-- Create databases for each service
CREATE DATABASE IF NOT EXISTS moderation_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS search_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS favorite_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS user_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS message_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS entry_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS auth_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS statistics_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS file_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS recommendation_svc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS eksiuser CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user if it doesn't exist
CREATE USER IF NOT EXISTS 'eksiuser_admin'@'localhost' IDENTIFIED BY 'user_password_123';
CREATE USER IF NOT EXISTS 'eksiuser_admin'@'%' IDENTIFIED BY 'user_password_123';

-- Grant privileges to the user for each database
GRANT ALL PRIVILEGES ON moderation_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON search_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON favorite_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON user_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON message_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON notification_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON entry_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON auth_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON statistics_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON file_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON recommendation_svc.* TO 'eksiuser_admin'@'localhost';
GRANT ALL PRIVILEGES ON eksiuser.* TO 'eksiuser_admin'@'localhost';

-- For connections from other hosts (like Docker containers)
GRANT ALL PRIVILEGES ON moderation_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON search_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON favorite_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON user_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON message_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON notification_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON entry_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON auth_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON statistics_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON file_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON recommendation_svc.* TO 'eksiuser_admin'@'%';
GRANT ALL PRIVILEGES ON eksiuser.* TO 'eksiuser_admin'@'%';

-- Apply the privileges
FLUSH PRIVILEGES;
