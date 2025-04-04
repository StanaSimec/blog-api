INSERT INTO category (id, header) VALUES (1, 'Design') ON CONFLICT DO NOTHING;
INSERT INTO category (id, header) VALUES (2, 'Sport') ON CONFLICT DO NOTHING;
INSERT INTO category (id, header) VALUES (3, 'Programming') ON CONFLICT DO NOTHING;
INSERT INTO category (id, header) VALUES (4, 'Books') ON CONFLICT DO NOTHING;
INSERT INTO category (id, header) VALUES (5, 'Cars') ON CONFLICT DO NOTHING;

INSERT INTO tag (id, header) VALUES (1, 'Weekly news') ON CONFLICT DO NOTHING;
INSERT INTO tag (id, header) VALUES (2, 'Java') ON CONFLICT DO NOTHING;
INSERT INTO tag (id, header) VALUES (3, 'Darts') ON CONFLICT DO NOTHING;
INSERT INTO tag (id, header) VALUES (4, 'Living room') ON CONFLICT DO NOTHING;
INSERT INTO tag (id, header) VALUES (5, 'SQL') ON CONFLICT DO NOTHING;

