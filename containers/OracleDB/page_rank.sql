
CREATE OR REPLACE TYPE float_array IS TABLE OF FLOAT;
/
CREATE OR REPLACE TYPE number_array IS TABLE OF NUMBER;
/
CREATE OR REPLACE TYPE graph IS TABLE OF float_array;
/

CREATE OR REPLACE FUNCTION from_number_to_float_array(
    p_numbers IN number_array
) RETURN float_array
AS
    v_result float_array := float_array();
BEGIN
    FOR i IN 1 .. p_numbers.COUNT LOOP
        v_result.EXTEND;
        v_result(i) := CAST(p_numbers(i) AS FLOAT);
    END LOOP;

    RETURN v_result;
END;

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
        v_row float_array;
    BEGIN
        SELECT p.id BULK COLLECT INTO v_post_ids FROM POST p 
        WHERE p.author_id IN (
            SELECT * FROM (SELECT user2_id FROM FRIENDSHIP WHERE user1_id = p_user_id ORDER BY interest) WHERE ROWNUM < p_best_friends_number   --best friend matching
            )
            OR p.author_id IN (
                SELECT * FROM (SELECT user2_id FROM FRIENDSHIP WHERE user1_id = p_user_id ORDER BY DBMS_RANDOM.RANDOM) WHERE ROWNUM < p_random_friends_number   -- random friend matching
            )
            OR p.category_id IN (
                SELECT * FROM (SELECT category_id FROM CATEGORY_INTEREST WHERE user_id = p_user_id ORDER BY interest ORDER BY interest) WHERE ROWNUM < p_best_friends_number  -- best category matching
            )
            OR p.author_id IN (
                SELECT author_id FROM POST WHERE ROWNUM <= 20 -- latest posts
            );
        v_post_rank_score := float_array();
        FOR i in v_post_ids.first..v_post_ids.last LOOP
            v_post_rank_score.EXTEND;
            v_post_rank_score(v_post_rank_score.COUNT) := feed_formulas.score(p_user_id, v_post_ids(i));
        END LOOP;
        FOR i IN v_post_ids.first..v_post_ids.last LOOP
            v_row := float_array();
            FOR j IN v_post_ids.first..v_post_ids.last LOOP
                v_row.EXTEND;
                IF i = j THEN
                    v_row(j) := v_post_rank_score(i);
                    --v_graph(i)(j) := v_post_rank_score(i);
                ELSE
                    SELECT author_id INTO v_author1_id FROM POST WHERE id = v_post_ids(i);
                    SELECT author_id INTO v_author2_id FROM POST WHERE id = v_post_ids(j);
                    IF v_author1_id = v_author2_id THEN
                        v_row(j) := 1;
                        --v_graph(i)(j) := 1;
                        --v_graph(j)(i) := 1;
                    ELSE 
                        v_row(j) := 0;
                        --v_graph(i)(j) := 0;
                        --v_graph(j)(i) := 0;
                    END IF;
                END IF;
            END LOOP;
            v_graph.EXTEND;
            v_graph(i) := v_row;
        END LOOP;
        --- add graph post ids labels to the last row
        v_graph.EXTEND;
        v_row := from_number_to_float_array(v_post_ids);
        v_graph(v_graph.COUNT) := v_row;
        RETURN v_graph;
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Backtrace     : ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
                RAISE;
    END generate;
END graph_generator;

/

exit;
