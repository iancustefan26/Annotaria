-- USERS
INSERT INTO USERS (username, password_hash, email) VALUES ('alice', 'hash1', 'alice@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('bob', 'hash2', 'bob@example.com');
INSERT INTO USERS (username, password_hash, email) VALUES ('carol', 'hash3', 'carol@example.com');

-- CATEGORIES
INSERT INTO CATEGORY (name) VALUES ('Photography');
INSERT INTO CATEGORY (name) VALUES ('Travel');
INSERT INTO CATEGORY (name) VALUES ('Food');

-- NAMED_TAGS
INSERT INTO NAMED_TAGS (name) VALUES ('Sunset');
INSERT INTO NAMED_TAGS (name) VALUES ('Mountain');
INSERT INTO NAMED_TAGS (name) VALUES ('Beach');

-- POSTS
INSERT INTO POST (author_id, category_id, creation_year, description) 
VALUES (1, 1, 2024, 'A beautiful sunset in the mountains');


INSERT INTO POST (author_id, category_id, creation_year, description) 
VALUES (2, 2, 2023, 'Exploring a hidden beach in Thailand');

INSERT INTO POST (author_id, category_id, creation_year, description) 
VALUES (3, 3, 2025, 'Delicious street food in Vietnam');

-- LIKES
INSERT INTO LIKES (user_id, post_id) VALUES (1, 2);  -- Alice likes Bob's post
INSERT INTO LIKES (user_id, post_id) VALUES (2, 1);  -- Bob likes Alice's post
INSERT INTO LIKES (user_id, post_id) VALUES (3, 1);  -- Carol likes Alice's post


-- COMMENTS
INSERT INTO COMMENTS (post_id, user_id, content) VALUES (1, 2, 'Amazing shot!');
INSERT INTO COMMENTS (post_id, user_id, content) VALUES (1, 3, 'Love the colors!');
INSERT INTO COMMENTS (post_id, user_id, content) VALUES (2, 1, 'Where exactly is this?');
INSERT INTO COMMENTS (post_id, user_id, content) VALUES (3, 2, 'Looks tasty!');
INSERT INTO COMMENTS (post_id, user_id, content) VALUES (3, 2, 'Can you share the recipe?');

INSERT INTO COMMENTS (post_id, user_id, content) VALUES (2, 1, 'Amazing shot!');


-- CATEGORY_FRAMES


-- NAMED_TAG_FRAMES
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (1, 1); -- Sunset
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (2, 1); -- Mountain
INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (3, 2); -- Beach

-- USER_TAG_FRAMES
-- Alice tags Carol in her post
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (1, 1, 3);
-- Bob tags Alice in his post
INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (2, 2, 1);

-- COMMIT
COMMIT;

/

EXIT;