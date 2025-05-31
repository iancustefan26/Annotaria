CREATE OR REPLACE TYPE statistics_record AS OBJECT (
    title VARCHAR2(256),
    owner_name VARCHAR2(256),
    score NUMBER
);
/
CREATE OR REPLACE TYPE statistics_table AS TABLE OF statistics_record;
/

CREATE OR REPLACE FUNCTION export_statistics(
    latest IN VARCHAR2 DEFAULT NULL   -- MONTH, YEAR, ALL TIME(NULL)
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
    v_table statistics_table := statistics_table();
    v_max NUMBER;
    v_author_name VARCHAR2(128);
    v_author_id NUMBER;
    v_post_id NUMBER;
BEGIN
    IF latest IS NULL THEN
        SELECT * INTO v_max, v_author_id
        FROM (SELECT likes_count, author_id FROM POST order by likes_count desc ) where rownum = 1;
        SELECT username INTO v_author_name FROM USERS WHERE id = v_author_id;
        v_table.EXTEND;
        v_table(v_table.COUNT) := statistics_record(
            'Most liked post of all time',
            v_author_name,
            v_max
            );
        SELECT * INTO v_max, v_author_id
        FROM (SELECT COMMENTS_COUNT, author_id FROM POST order by comments_count desc ) where rownum = 1;
        SELECT username INTO v_author_name FROM USERS WHERE id = v_author_id;
        v_table.EXTEND;
        v_table(v_table.COUNT) := statistics_record(
            'Most commented post of all time',
            v_author_name,
            v_max
            );
        SELECT popularity, author_id INTO v_max, v_author_id
        FROM (SELECT SUM(likes_count + comments_count) as popularity, author_id FROM POST group by author_id ORDER BY popularity DESC)
        WHERE ROWNUM = 1;
        SELECT username INTO v_author_name FROM USERS WHERE id = v_author_id;
        v_table.EXTEND;
        v_table(v_table.COUNT) := statistics_record(
            'Most popular user of all time',
            v_author_name,
            v_max
            );
    END IF;
    OPEN v_cursor FOR 
        SELECT * FROM TABLE(v_table);

    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;
/

SELECT * 
        FROM (SELECT COMMENTS_COUNT, author_id FROM POST order by comments_count desc ) where rownum = 1;
        SELECT username INTO v_author_name FROM USERS WHERE id = v_author_id;

select * from users;

commit;

exit;

delete from users_view where id = 12;

DECLARE
    c SYS_REFCURSOR;
    v_title VARCHAR2(256);
    v_owner_name VARCHAR2(256);
    v_score NUMBER;
BEGIN
    c := export_statistics(NULL);

    LOOP
        FETCH c INTO v_title, v_owner_name, v_score;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Title: ' || v_title || ' Owner: ' || v_owner_name || ' Score: ' || v_score);
    END LOOP;

    CLOSE c;
END;
/

commit;