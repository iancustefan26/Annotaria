DECLARE
  -- Variables for BLOB loading
  l_blob         BLOB;
  l_bfile        BFILE;
  l_len          INTEGER;

  -- Constants
  user_count         CONSTANT INTEGER := 50;
  named_tag_count    CONSTANT INTEGER := 10;
  post_count         CONSTANT INTEGER := 100;
  category_count     CONSTANT INTEGER := 5; -- for example

  -- Cursor to get users and posts
  CURSOR users_cur IS SELECT id FROM USERS;
  CURSOR posts_cur IS SELECT id FROM POST;

  -- Procedure to load blob from file
  PROCEDURE load_blob_from_file(p_dir VARCHAR2, p_filename VARCHAR2, p_blob OUT BLOB) IS
  BEGIN
    DBMS_LOB.createtemporary(p_blob, TRUE);
    l_bfile := BFILENAME(p_dir, p_filename);
    DBMS_LOB.fileopen(l_bfile, DBMS_LOB.file_readonly);
    DBMS_LOB.loadfromfile(p_blob, l_bfile, DBMS_LOB.getlength(l_bfile));
    DBMS_LOB.fileclose(l_bfile);
  END;

BEGIN
  -- 1. Insert Users
  FOR i IN 1..user_count LOOP
    INSERT INTO USERS (username, password_hash, email)
    VALUES (
      'user' || i,
      STANDARD_HASH('password' || i, 'SHA1'),
      'user' || i || '@example.com'
    );
  END LOOP;

  COMMIT;

  -- 2. Insert Categories
  FOR i IN 1..category_count LOOP
    INSERT INTO CATEGORY (name) VALUES ('Category' || i);
  END LOOP;

  COMMIT;

  -- 3. Insert Named Tags
  FOR i IN 1..named_tag_count LOOP
    INSERT INTO NAMED_TAGS (name) VALUES ('tag' || i);
  END LOOP;

  COMMIT;

  -- 4. Insert Posts (Photos)
  FOR i IN 1..50 LOOP
    load_blob_from_file('PHOTOS_DIR', 'photo' || i || '.jpg', l_blob);

    INSERT INTO POST
    VALUES (
      DEFAULT,
      MOD(i, user_count) + 1,              -- author_id
      MOD(i, category_count) + 1,          -- category_id
      l_blob,                              -- media_blob
      NULL,                                -- external_media_url
      'image',                             -- media_type
      2025,                                -- creation_year
      DEFAULT,                             -- date_posted
      'Photo post #' || i,                 -- description
      DEFAULT,                             -- likes_count
      DEFAULT                              -- comments_count
    );
  END LOOP;

  -- 5. Insert Posts (Videos)
  FOR i IN 1..50 LOOP
    load_blob_from_file('VIDEOS_DIR', 'video' || i || '.mp4', l_blob);

    INSERT INTO POST
    VALUES (
      DEFAULT,
      MOD(i + 50, user_count) + 1,         -- author_id
      MOD(i + 1, category_count) + 1,      -- category_id
      l_blob,                              -- media_blob
      NULL,                                -- external_media_url
      'video',                             -- media_type
      2025,                                -- creation_year
      DEFAULT,                             -- date_posted
      'Video post #' || i,                 -- description
      DEFAULT,                             -- likes_count
      DEFAULT                              -- comments_count
    );
  END LOOP;

  COMMIT;

  -- 6. Insert More Uniform Likes (5–20 likes per post)
  FOR p IN (SELECT id FROM POST) LOOP
    FOR i IN 1..TRUNC(DBMS_RANDOM.VALUE(5, 21)) LOOP
      BEGIN
        INSERT INTO LIKES (user_id, post_id)
        VALUES (
          MOD(p.id + i * 7, user_count) + 1,  -- pseudo-random user_id
          p.id
        );
      EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN NULL;
      END;
    END LOOP;
  END LOOP;

  COMMIT;

  -- 7. Insert More Uniform Comments (3–10 comments per post)
  FOR p IN (SELECT id FROM POST) LOOP
    FOR i IN 1..TRUNC(DBMS_RANDOM.VALUE(3, 11)) LOOP
      INSERT INTO COMMENTS (post_id, user_id, content)
      VALUES (
        p.id,
        MOD(p.id + i * 13, user_count) + 1,
        'Comment ' || i || ' on post #' || p.id
      );
    END LOOP;
  END LOOP;

  COMMIT;

  -- 8. Insert Named Tag Frames (1-3 tags per post)
  FOR p IN (SELECT id FROM POST) LOOP
    FOR i IN 1..3 LOOP
      INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id)
      VALUES (MOD(p.id + i, named_tag_count) + 1, p.id);
    END LOOP;
  END LOOP;

  COMMIT;

  -- 9. Insert User Tag Frames
  FOR p IN (SELECT id, author_id FROM POST) LOOP
    INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id)
    VALUES (p.id, p.author_id, MOD(p.author_id, user_count) + 1);
  END LOOP;

  COMMIT;

END;
/

exit;

