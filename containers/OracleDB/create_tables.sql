-- 1. USER
CREATE TABLE USERS (
  id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username        VARCHAR2(50)   NOT NULL UNIQUE,
  password_hash   VARCHAR2(512)  NOT NULL,
  email           VARCHAR2(255)  NOT NULL UNIQUE
);

-- 2. CATEGORY
CREATE TABLE CATEGORY (
  id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name            VARCHAR2(100)  NOT NULL UNIQUE
);

-- 3. NAMED_TAGS
CREATE TABLE NAMED_TAGS (
  id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name            VARCHAR2(100)  NOT NULL UNIQUE
);

-- 4. POST
CREATE TABLE POST (
  id               NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  author_id        NUMBER  NOT NULL                       REFERENCES USERS(id)       ON DELETE CASCADE,
  category_id      NUMBER                        REFERENCES CATEGORY(id)   ON DELETE CASCADE,
  media_blob       BLOB,
  external_media_url VARCHAR2(512),
  creation_year    NUMBER(4),
  date_posted      TIMESTAMP DEFAULT SYSTIMESTAMP,
  description      VARCHAR2(2000),
  likes_count      NUMBER        DEFAULT 0        NOT NULL,
  comments_count   NUMBER        DEFAULT 0        NOT NULL
);

-- 5. LIKES
CREATE TABLE LIKES (
  user_id   NUMBER NOT NULL REFERENCES USERS(id)     ON DELETE CASCADE,
  post_id   NUMBER NOT NULL REFERENCES POST(id)       ON DELETE CASCADE,  
  CONSTRAINT pk_like PRIMARY KEY(user_id, post_id)
);

-- 6. COMMENTS
CREATE TABLE COMMENTS (
  id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  post_id    NUMBER NOT NULL REFERENCES POST(id)     ON DELETE CASCADE,
  user_id    NUMBER     REFERENCES USERS(id)         ON DELETE CASCADE,
  content    VARCHAR2(1000)       NOT NULL,
  date_posted TIMESTAMP  DEFAULT SYSTIMESTAMP
);

-- 7. CATEGORY_FRAMES (M–N între CATEGORY și POST)
CREATE TABLE CATEGORY_FRAMES (
  category_id NUMBER NOT NULL REFERENCES CATEGORY(id) ON DELETE CASCADE,
  post_id     NUMBER NOT NULL REFERENCES POST(id)     ON DELETE CASCADE,
  CONSTRAINT pk_cat_frames PRIMARY KEY(category_id, post_id)
);

-- 8. NAMED_TAG_FRAMES (M–N între NAMED_TAGS și POST)
CREATE TABLE NAMED_TAG_FRAMES (
  named_tag_id NUMBER NOT NULL REFERENCES NAMED_TAGS(id) ON DELETE CASCADE,
  post_id      NUMBER NOT NULL REFERENCES POST(id)       ON DELETE CASCADE,
  CONSTRAINT pk_named_tag_frames PRIMARY KEY(named_tag_id, post_id)
);

-- 9. USER_TAG_FRAMES
--    un user A etichetează un user B în cadrul unui post
CREATE TABLE USER_TAG_FRAMES (
  post_id         NUMBER NOT NULL REFERENCES POST(id)       ON DELETE CASCADE,
  user_author_id  NUMBER NOT NULL REFERENCES USERS(id)     ON DELETE CASCADE,
  user_tagged_id  NUMBER NOT NULL REFERENCES USERS(id)     ON DELETE CASCADE,
  CONSTRAINT pk_user_tag_frames PRIMARY KEY(post_id, user_author_id, user_tagged_id)
);

-- INDEXURI
CREATE INDEX idx_post_date     ON POST(date_posted);
CREATE INDEX idx_comments_post ON COMMENTS(post_id);

-- COMMIT
COMMIT;
exit;