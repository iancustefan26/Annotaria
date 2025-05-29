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
  FROM POST 
  WHERE id = p_id;
  
  IF l_count = 0 THEN
    RAISE post_exceptions.no_such_post;
  END IF;
  
  DELETE FROM POST
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
    WHERE author_id = p_user_id;
    
  RETURN l_cursor;
  
EXCEPTION
  WHEN post_exceptions.no_such_post THEN
    RAISE_APPLICATION_ERROR(-20003, 'No posts found for user ID ' || p_user_id);
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(-20107, 'Error retrieving posts by user ID: ' || SQLERRM);
END get_post_by_user_id;
/

