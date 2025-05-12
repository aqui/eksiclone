CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME,
    last_updated_date DATETIME,
    title VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_by BIGINT,
    is_trending BOOLEAN DEFAULT FALSE,
    view_count BIGINT DEFAULT 0,
    entry_count BIGINT DEFAULT 0,
    version BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE topic_tags (
    topic_id BIGINT,
    tag VARCHAR(50),
    PRIMARY KEY (topic_id, tag),
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);