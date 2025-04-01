CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    header VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS article (
    id SERIAL PRIMARY KEY,
    header VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    category_id SERIAL REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS tag (
    id SERIAL PRIMARY KEY,
    header VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS article_tag (
    article_id SERIAL REFERENCES article (id) ON DELETE CASCADE,
    tag_id SERIAL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);