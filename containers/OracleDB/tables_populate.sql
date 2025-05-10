SET DEFINE OFF;
-- 1. UTILIZATORI
INSERT INTO USERS(username, password_hash, email) VALUES
  ('alice',   'hash_alice',   'alice@example.com'),
  ('bob',     'hash_bob',     'bob@example.com'),
  ('charlie', 'hash_charlie', 'charlie@example.com');

-- 2. CATEGORII
INSERT INTO CATEGORY(name) VALUES
  ('Photography'),
  ('Videography'),
  ('Art');

-- 3. ETICHETE DENUMITE
INSERT INTO NAMED_TAGS(name) VALUES
  ('#sunset'),
  ('#portrait'),
  ('#landscape');

-- 4. POSTĂRI
INSERT INTO POST(author_id, category_id, media_blob, external_media_url, creation_year, description)
VALUES
  (1, 1, NULL, 'https://example.com/img1.jpg', 2024, 'My first sunset photo'),
  (2, 2, NULL, 'https://example.com/video1.mp4', 2023, 'Quick timelapse'),
  (1, 3, NULL, NULL,                 2024, 'Sketch I did yesterday'),
  (3, 1, NULL, 'https://example.com/img2.png', 2022, 'Mountain landscape'),
  (2, 3, NULL, NULL,                 2024, 'Abstract art piece');

-- 5. LIKE-uri
INSERT INTO LIKES(user_id, post_id) VALUES
  (2, 1),  -- bob dă like pozei lui alice
  (3, 1),  -- charlie dă like pozei lui alice
  (1, 2),  -- alice dă like videoului lui bob
  (1, 4);  -- alice dă like peisajului lui charlie

-- 6. COMENTARII
INSERT INTO COMMENTS(post_id, user_id, content) VALUES
  (1, 2, 'Great shot!'),
  (1, 3, 'Amazing colors.'),
  (2, 1, 'Cool timelapse!'),
  (4, 1, 'Love this view.');

-- 7. CATEGORY_FRAMES
INSERT INTO CATEGORY_FRAMES(category_id, post_id) VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (1, 4),
  (3, 5);

-- 8. NAMED_TAG_FRAMES
INSERT INTO NAMED_TAG_FRAMES(named_tag_id, post_id) VALUES
  (1, 1),  -- #sunset pe post 1
  (2, 1),  -- #portrait pe post 1
  (3, 4),  -- #landscape pe post 4
  (1, 4),  -- #sunset pe post 4
  (3, 5);  -- #landscape pe post 5

-- 9. USER_TAG_FRAMES
--   alice îl etichetează pe bob pe post 2, bob îl etichetează pe charlie pe post 4
INSERT INTO USER_TAG_FRAMES(post_id, user_author_id, user_tagged_id) VALUES
  (2, 1, 2),
  (4, 2, 3);

COMMIT;
EXIT;
