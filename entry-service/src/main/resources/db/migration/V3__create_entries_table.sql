CREATE TABLE entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME,
    last_updated_date DATETIME,
    content TEXT NOT NULL,
    topic_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    favorite_count BIGINT DEFAULT 0,
    is_edited BOOLEAN DEFAULT FALSE,
    version BIGINT,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index to speed up entry retrieval by topic
CREATE INDEX idx_entries_topic_id ON entries(topic_id);

-- Index to speed up entry retrieval by author
CREATE INDEX idx_entries_author_id ON entries(author_id);

-- Index to speed up search in content 
CREATE FULLTEXT INDEX idx_entries_content ON entries(content);