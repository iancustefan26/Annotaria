
CREATE OR REPLACE TYPE float_array IS TABLE OF FLOAT;
/
CREATE OR REPLACE TYPE number_array IS TABLE OF NUMBER;
/
CREATE OR REPLACE TYPE graph IS TABLE OF float_array;
/
CREATE OR REPLACE PACKAGE graph_generator
as
    FUNCTION generate(
        p_user_id IN NUMBER,
        p_best_friends_number IN NUMBER,
        p_random_friends_number IN NUMBER
    ) RETURN graph;
END graph_generator;

/

CREATE OR REPLACE PACKAGE BODY graph_generator
AS

    FUNCTION generate(
        p_user_id NUMBER,
        p_best_friends_number IN NUMBER,
        p_random_friends_number IN NUMBER
    ) RETURN graph
    AS
        v_post_ids number_array; --:= number_array(p_best_friends_number + p_random_friends_number);
        v_post_rank_score float_array; -- := float_array(p_best_friends_number + p_random_friends_number);
        v_graph graph := graph();
        v_author1_id NUMBER;
        v_author2_id NUMBER;
    BEGIN
        SELECT p.id BULK COLLECT INTO v_post_ids FROM POST p 
        WHERE p.author_id IN (
            SELECT * FROM (SELECT user2_id FROM FRIENDSHIP WHERE user1_id = p_user_id) WHERE ROWNUM < p_best_friends_number
            );
        v_post_rank_score := float_array();
        FOR i in v_post_ids.first..v_post_ids.last LOOP
            v_post_rank_score(i):= feed_fromulas.score(p_user_id, v_post_ids(i));
        END LOOP;
        FOR i IN v_post_ids.first..v_post_ids.last LOOP
            FOR j IN v_post_ids.first..v_post_ids.last LOOP
                IF i = j THEN
                    v_graph(i)(j) := v_post_rank_score(i);
                ELSE
                    SELECT author_id INTO v_author1_id FROM POST WHERE id = v_post_ids(i);
                    SELECT author_id INTO v_author2_id FROM POST WHERE id = v_post_ids(j);
                    IF v_author1_id = v_author2_id THEN
                        v_graph(i)(j) := 1;
                        v_graph(j)(i) := 1;
                    ELSE 
                        v_graph(i)(j) := 0;
                        v_graph(j)(i) := 0;
                    END IF;
                END IF;
            END LOOP;
        END LOOP;
    END generate;

END graph_generator;

/

exit;