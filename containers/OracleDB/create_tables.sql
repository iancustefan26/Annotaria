-- Create table for categories
CREATE TABLE categories (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR2(255) NOT NULL
);

-- Create table for users
CREATE TABLE users (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password_hash VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) NOT NULL UNIQUE
);

-- Create table for posts
CREATE TABLE posts (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    authorid NUMBER NOT NULL,
    categoryid NUMBER NOT NULL,
    media_blob VARCHAR2(4000),
    external_media_url VARCHAR2(4000),
    creation_year NUMBER,
    date_posted DATE NOT NULL,
    description VARCHAR2(4000),
    likes_count NUMBER DEFAULT 0,
    comments_count NUMBER DEFAULT 0,
    CONSTRAINT fk_posts_author FOREIGN KEY (authorid) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_posts_category FOREIGN KEY (categoryid) REFERENCES categories(id)
);

-- Create table for name_tags
CREATE TABLE name_tags (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    postid NUMBER NOT NULL,
    name VARCHAR2(255) NOT NULL,
    CONSTRAINT fk_name_tags_post FOREIGN KEY (postid) REFERENCES posts(id) ON DELETE CASCADE
);

-- Create table for user_tags
CREATE TABLE user_tags (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    postid NUMBER NOT NULL,
    user_taggedid NUMBER NOT NULL,
    user_authorid NUMBER NOT NULL,
    CONSTRAINT fk_user_tags_post FOREIGN KEY (postid) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_tags_tagged FOREIGN KEY (user_taggedid) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_tags_author FOREIGN KEY (user_authorid) REFERENCES users(id) ON DELETE CASCADE
);

-- Create table for comments
CREATE TABLE comments (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    postid NUMBER NOT NULL,
    userid NUMBER NOT NULL,
    content VARCHAR2(4000) NOT NULL,
    CONSTRAINT fk_comments_post FOREIGN KEY (postid) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (userid) REFERENCES users(id) ON DELETE CASCADE
);

-- Create table for likes
CREATE TABLE likes (
    id NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    userid NUMBER NOT NULL,
    postid NUMBER NOT NULL,
    CONSTRAINT fk_likes_user FOREIGN KEY (userid) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_post FOREIGN KEY (postid) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT uk_likes UNIQUE (userid, postid)
);

COMMIT;
EXIT;

