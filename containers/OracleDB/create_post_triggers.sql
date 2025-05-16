
CREATE or replace TRIGGER no_more_than_50_tags
FOR INSERT ON USER_TAG_FRAMES
COMPOUND TRIGGER

    v_tag_count NUMBER := 0;

    AFTER EACH ROW IS
    BEGIN
        v_tag_count := v_tag_count + 1;
        IF v_tag_count > 50 THEN
            ROLLBACK;
            DBMS_OUTPUT.PUT_LINE('You cannot tag more than 50 users on a post (spam violation reasons)');
            raise tag_exceptions.over_50_user_tags;
        END IF;
        -- incerement friendship relation between the user that tagged and the tagged user
        -- maybe creating another table for this (depends on the algorithm that will be used)
    END AFTER EACH ROW;


END no_more_than_50_tags;
/

commit;

exit;