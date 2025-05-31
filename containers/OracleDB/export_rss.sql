CREATE OR REPLACE TYPE rss_record AS OBJECT (
    title VARCHAR2(256),
    link VARCHAR2(256),
    description VARCHAR2(2000),
    pub_date TIMESTAMP,
    guid NUMBER
);
/
CREATE OR REPLACE TYPE rss_table AS TABLE OF rss_record;
/

CREATE OR REPLACE FUNCTION export_rss(
    p_latest_posts NUMBER DEFAULT 20,
    p_latest_comments NUMBER DEFAULT 20
) 
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT * FROM (
            -- Get 20 latest posts
            SELECT
                u.username || ' posted a something new about ' || c.name AS title,
                'post?id=' || p.id AS link,
                p.description AS description,
                p.date_posted AS pub_date,
                p.id AS guid
            FROM POST p JOIN USERS u ON p.author_id = u.id JOIN CATEGORY c ON c.id = p.category_id
            ORDER BY p.date_posted DESC
            ) WHERE ROWNUM <= p_latest_posts

            UNION 

            -- Get 20 latest comments
            SELECT * FROM(
                 SELECT
                username1 || ' commented on ' || username2 || '''s post' AS title,
                '/post?id=' || post_id AS link,
                cnt AS description,
                dp AS pub_date,
                i + 1000000 AS guid  -- offset for guid collisions
            FROM (SELECT post_id, u.username as username1, username2, dp, i, cnt FROM (SELECT p.id as post_id, c.user_id as i1, u.username as username2, c.date_posted as dp, c.id as i, c.content as cnt
                FROM comments c JOIN POST p ON p.id = c.post_id JOIN USERS u ON u.id = p.author_id) JOIN USERS u ON i1 = u.id)
            ORDER BY dp DESC
            ) WHERE ROWNUM <= p_latest_comments
        ORDER BY pub_date DESC;

    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;

/

commit;
exit;

DECLARE
    v_cursor SYS_REFCURSOR;
    v_title       VARCHAR2(256);
    v_link        VARCHAR2(256);
    v_description VARCHAR2(2000);
    v_pub_date    TIMESTAMP;
    v_guid        NUMBER;
BEGIN
    -- Call the function
    v_cursor := export_rss;

    -- Loop through the results
    LOOP
        FETCH v_cursor INTO v_title, v_link, v_description, v_pub_date, v_guid;
        EXIT WHEN v_cursor%NOTFOUND;

        -- Output results
        DBMS_OUTPUT.PUT_LINE('Title: ' || v_title);
        DBMS_OUTPUT.PUT_LINE('Link: ' || v_link);
        DBMS_OUTPUT.PUT_LINE('Date: ' || TO_CHAR(v_pub_date, 'YYYY-MM-DD HH24:MI:SS'));
        DBMS_OUTPUT.PUT_LINE('Description: ' || v_description);
        DBMS_OUTPUT.PUT_LINE('GUID: ' || v_guid);
        DBMS_OUTPUT.PUT_LINE('--------------------------');
    END LOOP;

    CLOSE v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        IF v_cursor%ISOPEN THEN
            CLOSE v_cursor;
        END IF;
        RAISE;
END;

/

commit;

select * from user_source where name like '%FEED%';

select * from users;