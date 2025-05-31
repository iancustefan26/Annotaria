-- Get post by ID
CREATE OR REPLACE FUNCTION get_post_by_id(p_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
  l_cursor SYS_REFCURSOR;
  l_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO l_count 
  FROM POST 
  WHERE id = p_id;
  
  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;
  
  OPEN l_cursor FOR
    SELECT id, author_id, category_id, media_blob, external_media_url, 
           creation_year, date_posted, description, likes_count, comments_count
    FROM POST
    WHERE id = p_id;
    
  RETURN l_cursor;
  
EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20003, 'Post with ID ' || p_id || ' not found');
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20103, 'Error retrieving post by ID: ' || SQLERRM);
END get_post_by_id;
/



-- Delete post by ID
CREATE OR REPLACE PROCEDURE delete_post_by_id(p_id IN NUMBER)
AS
  l_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO l_count 
  FROM POSTS_VIEW
  WHERE id = p_id;
  
  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;
  
  DELETE FROM POSTS_VIEW
  WHERE id = p_id;
  
EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20003, 'Post with ID ' || p_id || ' not found');
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20103, 'Error deleting post by ID: ' || SQLERRM);
END delete_post_by_id;
/



-- Get posts by category
CREATE OR REPLACE FUNCTION get_post_by_category(p_category_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
  l_cursor SYS_REFCURSOR;
  l_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO l_count 
  FROM POST 
  WHERE category_id = p_category_id;
  
  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;
  
  OPEN l_cursor FOR
    SELECT id, author_id, category_id, media_blob, external_media_url, 
           creation_year, date_posted, description, likes_count, comments_count
    FROM POST
    WHERE category_id = p_category_id;
    
  RETURN l_cursor;
  
EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20003, 'No posts found for category ID ' || p_category_id);
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20105, 'Error retrieving posts by category: ' || SQLERRM);
END get_post_by_category;
/

-- Get posts by user ID
CREATE OR REPLACE FUNCTION get_post_by_user_id(p_user_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
  l_cursor SYS_REFCURSOR;
  l_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO l_count 
  FROM POST 
  WHERE author_id = p_user_id;
  
  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;
  
  OPEN l_cursor FOR
    SELECT id, author_id, category_id, media_blob, external_media_url, 
           creation_year, date_posted, description, likes_count, comments_count
    FROM POST
    WHERE author_id = p_user_id order by date_posted;
    
  RETURN l_cursor;
  
EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20003, 'No posts found for user ID ' || p_user_id);
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20107, 'Error retrieving posts by user ID: ' || SQLERRM);
END get_post_by_user_id;
/

CREATE OR REPLACE FUNCTION get_saved_posts_by_user_id(p_user_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
  l_cursor SYS_REFCURSOR;
  l_count NUMBER;
BEGIN
  -- Check if the user has any saved posts
  SELECT COUNT(*) INTO l_count
  FROM SAVED_POSTS
  WHERE user_id = p_user_id;

  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;

  -- Open a cursor for the saved posts
  OPEN l_cursor FOR
    SELECT 
      p.id, 
      p.author_id, 
      p.category_id, 
      p.media_blob, 
      p.external_media_url,
      p.creation_year, 
      p.date_posted, 
      p.description, 
      p.likes_count, 
      p.comments_count
    FROM 
      POST p
    JOIN 
      SAVED_POSTS s ON p.id = s.post_id
    WHERE 
      s.user_id = p_user_id
    ORDER BY 
      p.date_posted;

  RETURN l_cursor;

EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20004, 'No saved posts found for user ID ' || p_user_id);
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20108, 'Error retrieving saved posts by user ID: ' || SQLERRM);
END get_saved_posts_by_user_id;
/


