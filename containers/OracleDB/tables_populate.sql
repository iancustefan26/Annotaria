-- Populate USERS
BEGIN
  FOR i IN 1..15 LOOP
    INSERT INTO USERS (username, password_hash, email)
    VALUES (
      'user' || i,
      'hash' || i,
      'user' || i || '@example.com'
    );
  END LOOP;
END;
/

-- Populate CATEGORY
BEGIN
  FOR i IN 1..5 LOOP
    INSERT INTO CATEGORY (name) VALUES ('Category ' || i);
  END LOOP;
END;
/

-- Populate NAMED_TAGS
BEGIN
  FOR i IN 1..5 LOOP
    INSERT INTO NAMED_TAGS (name) VALUES ('Tag' || i);
  END LOOP;
END;
/

-- Populate POSTS
BEGIN
  FOR i IN 1..30 LOOP
    INSERT INTO POST (
      author_id,
      category_id,
      creation_year,
      external_media_url,
      description
    )
    VALUES (
      MOD(i, 15) + 1,
      MOD(i, 5) + 1,
      2020 + MOD(i, 5),
      'http://media.example.com/' || i,
      'Sample description for post ' || i
    );
  END LOOP;
END;
/

-- Populate LIKES
-- Safe unique likes insert: Max 40 unique likes
DECLARE
  v_user_id NUMBER;
  v_post_id NUMBER;
  v_count   NUMBER := 0;
BEGIN
  FOR i IN 1..1000 LOOP  -- Big enough range to get 40 uniques
    EXIT WHEN v_count >= 40;
    v_user_id := MOD(i, 15) + 1;
    v_post_id := MOD(i * 7, 30) + 1;

    BEGIN
      INSERT INTO LIKES (user_id, post_id)
      VALUES (v_user_id, v_post_id);
      v_count := v_count + 1;
    EXCEPTION
      WHEN DUP_VAL_ON_INDEX THEN
        NULL; -- Skip duplicates
    END;
  END LOOP;
END;
/


-- Populate COMMENTS
BEGIN
  FOR i IN 1..20 LOOP
    INSERT INTO COMMENTS (post_id, user_id, content)
    VALUES (
      MOD(i, 30) + 1,
      MOD(i * 3, 15) + 1,
      'This is a comment ' || i
    );
  END LOOP;
END;
/

-- Populate CATEGORY_FRAMES
BEGIN
  FOR i IN 1..30 LOOP
    INSERT INTO CATEGORY_FRAMES (category_id, post_id)
    VALUES (
      MOD(i, 5) + 1,
      i
    );
  END LOOP;
END;
/

-- Populate NAMED_TAG_FRAMES
BEGIN
  FOR i IN 1..30 LOOP
    INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id)
    VALUES (
      MOD(i, 5) + 1,
      i
    );
  END LOOP;
END;
/

-- Populate USER_TAG_FRAMES
BEGIN
  FOR i IN 1..10 LOOP
    INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id)
    VALUES (
      MOD(i, 30) + 1,
      MOD(i, 15) + 1,
      MOD(i * 2, 15) + 1
    );
  END LOOP;
END;
/

COMMIT;
exit;
