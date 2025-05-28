-- USERS
INSERT INTO USERS (username, password_hash, email) VALUES ('alice', 'hash1', 'alice@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('bob', 'hash2', 'bob@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('carol', 'hash3', 'carol@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('dave', 'hash4', 'dave@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('eve', 'hash5', 'eve@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('frank', 'hash6', 'frank@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('grace', 'hash7', 'grace@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('heidi', 'hash8', 'heidi@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('ivan', 'hash9', 'ivan@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('judy', 'hash10', 'judy@example.com');

-- CATEGORIES
INSERT INTO CATEGORY (name) VALUES ('Photography');
INSERT INTO CATEGORY (name) VALUES ('Travel');
INSERT INTO CATEGORY (name) VALUES ('Food');
INSERT INTO CATEGORY (name) VALUES ('Fitness');
INSERT INTO CATEGORY (name) VALUES ('Technology');

-- NAMED_TAGS
INSERT INTO NAMED_TAGS (name) VALUES ('Sunset');
INSERT INTO NAMED_TAGS (name) VALUES ('Mountain');
INSERT INTO NAMED_TAGS (name) VALUES ('Beach');
INSERT INTO NAMED_TAGS (name) VALUES ('Coding');
INSERT INTO NAMED_TAGS (name) VALUES ('Workout');

-- POSTS (author_id, category_id, creation_year, description)
INSERT INTO POST VALUES (DEFAULT, 1, 1, NULL, NULL, 2024, DEFAULT, 'Sunset over the hills', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 2, 2, NULL, NULL, 2023, DEFAULT, 'Exploring the Amazon rainforest', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 3, 3, NULL, NULL, 2022, DEFAULT, 'Best street food in Bangkok', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 4, 4, NULL, NULL, 2025, DEFAULT, 'Morning run around the lake', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 5, 5, NULL, NULL, 2025, DEFAULT, 'Building my first robot', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 6, 1, NULL, NULL, 2023, DEFAULT, 'City skyline at night', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 7, 2, NULL, NULL, 2022, DEFAULT, 'Desert adventure', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 8, 3, NULL, NULL, 2023, DEFAULT, 'Italian pasta secrets', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 9, 4, NULL, NULL, 2024, DEFAULT, 'Gym gains progress', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 10, 5, NULL, NULL, 2025, DEFAULT, 'AI is taking over!', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 1, 1, NULL, NULL, 2025, DEFAULT, 'Foggy forest morning', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 2, 2, NULL, NULL, 2024, DEFAULT, 'Lost temples of Cambodia', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 3, 3, NULL, NULL, 2022, DEFAULT, 'Vietnamese Pho bowl', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 4, 4, NULL, NULL, 2023, DEFAULT, '5K marathon training', DEFAULT, DEFAULT);
INSERT INTO POST VALUES (DEFAULT, 5, 5, NULL, NULL, 2024, DEFAULT, 'Hackathon win!', DEFAULT, DEFAULT);

-- LIKES
-- Every user likes 3 different posts, no duplicates
INSERT INTO LIKES (user_id, post_id) VALUES (1, 2);
INSERT INTO LIKES (user_id, post_id) VALUES (1, 3);
INSERT INTO LIKES (user_id, post_id) VALUES (1, 5);

INSERT INTO LIKES (user_id, post_id) VALUES (2, 1);
INSERT INTO LIKES (user_id, post_id) VALUES (2, 4);
INSERT INTO LIKES (user_id, post_id) VALUES (2, 6);

INSERT INTO LIKES (user_id, post_id) VALUES (3, 2);
INSERT INTO LIKES (user_id, post_id) VALUES (3, 7);
INSERT INTO LIKES (user_id, post_id) VALUES (3, 8);

INSERT INTO LIKES (user_id, post_id) VALUES (4, 9);
INSERT INTO LIKES (user_id, post_id) VALUES (4, 10);
INSERT INTO LIKES (user_id, post_id) VALUES (4, 3);

INSERT INTO LIKES (user_id, post_id) VALUES (5, 11);
INSERT INTO LIKES (user_id, post_id) VALUES (5, 12);
INSERT INTO LIKES (user_id, post_id) VALUES (5, 13);

INSERT INTO LIKES (user_id, post_id) VALUES (6, 1);
INSERT INTO LIKES (user_id, post_id) VALUES (6, 5);
INSERT INTO LIKES (user_id, post_id) VALUES (6, 15);

INSERT INTO LIKES (user_id, post_id) VALUES (7, 4);
INSERT INTO LIKES (user_id, post_id) VALUES (7, 7);
INSERT INTO LIKES (user_id, post_id) VALUES (7, 8);

INSERT INTO LIKES (user_id, post_id) VALUES (8, 2);
INSERT INTO LIKES (user_id, post_id) VALUES (8, 10);
INSERT INTO LIKES (user_id, post_id) VALUES (8, 14);

INSERT INTO LIKES (user_id, post_id) VALUES (9, 9);
INSERT INTO LIKES (user_id, post_id) VALUES (9, 13);
INSERT INTO LIKES (user_id, post_id) VALUES (9, 15);

INSERT INTO LIKES (user_id, post_id) VALUES (10, 1);
INSERT INTO LIKES (user_id, post_id) VALUES (10, 3);
INSERT INTO LIKES (user_id, post_id) VALUES (10, 12);

-- COMMENTS (2 comments per post from different users)
BEGIN
  FOR i IN 1..15 LOOP
    INSERT INTO COMMENTS (post_id, user_id, content) VALUES (i, MOD(i, 10) + 1, 'Nice post ' || i || '!');
    INSERT INTO COMMENTS (post_id, user_id, content) VALUES (i, MOD(i+3, 10) + 1, 'Love it! #' || i);
  END LOOP;
END;
/

-- NAMED_TAG_FRAMES (some tags reused across multiple posts)
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (1, 1);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (1, 6);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (2, 2);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (2, 11);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (3, 3);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (3, 7);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (4, 5);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (4, 10);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (5, 4);
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (5, 9);

-- USER_TAG_FRAMES (tag some users in posts by others)
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (1, 1, 2);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (2, 2, 3);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (3, 3, 4);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (4, 4, 5);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (5, 5, 6);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (6, 6, 7);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (7, 7, 8);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (8, 8, 9);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (9, 9, 10);
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (10, 10, 1);

-- COMMIT
COMMIT;

exit;