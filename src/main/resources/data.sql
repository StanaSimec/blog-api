INSERT INTO category (id, header) VALUES (1, 'Design');
INSERT INTO category (id, header) VALUES (2, 'Sport');
INSERT INTO category (id, header) VALUES (3, 'Programming');
INSERT INTO category (id, header) VALUES (4, 'Books');
INSERT INTO category (id, header) VALUES (5, 'Cars');

INSERT INTO tag (id, header) VALUES (1, 'Weekly news');
INSERT INTO tag (id, header) VALUES (2, 'Java');
INSERT INTO tag (id, header) VALUES (3, 'Darts');
INSERT INTO tag (id, header) VALUES (4, 'Living room');
INSERT INTO tag (id, header) VALUES (5, 'SQL');

INSERT INTO article (header, content, created_at, updated_at, category_id) VALUES ('New Java release', 'New Java is on way to customers', NOW(), NOW(), 3);

INSERT INTO article_tag(article_id, tag_id) VALUES (1, 1);
INSERT INTO article_tag(article_id, tag_id) VALUES (1, 2);

