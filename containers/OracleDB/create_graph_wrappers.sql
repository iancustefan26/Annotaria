CREATE OR REPLACE TYPE matrix_cell AS OBJECT (
    row_index INTEGER,
    col_index INTEGER,
    value FLOAT
);
/

CREATE OR REPLACE TYPE matrix_table AS TABLE OF matrix_cell;
/


CREATE OR REPLACE FUNCTION wrap_float_array(
    p_array IN float_array
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT COLUMN_VALUE AS val
        FROM TABLE(p_array); -- unnest the array for cursor use
    RETURN v_cursor;
END;

/

CREATE OR REPLACE FUNCTION graph_to_cursor (
    p_user_id IN NUMBER,
    p_best_friends_number IN NUMBER,
    p_random_friends_number IN NUMBER,
    p_category_id IN NUMBER DEFAULT NULL
) RETURN SYS_REFCURSOR
AS
    v_graph graph;
    v_matrix matrix_table := matrix_table();
    v_row float_array;
    v_cursor SYS_REFCURSOR;
BEGIN
    v_graph := graph_generator.generate(p_user_id, p_best_friends_number, p_random_friends_number, p_category_id);

    FOR i IN v_graph.FIRST .. v_graph.LAST LOOP
        v_row := v_graph(i);
        FOR j IN v_row.FIRST .. v_row.LAST LOOP
            v_matrix.EXTEND;
            v_matrix(v_matrix.COUNT) := matrix_cell(i, j, v_row(j));
        END LOOP;
    END LOOP;

    OPEN v_cursor FOR
        SELECT * FROM TABLE(v_matrix);

    RETURN v_cursor;
END;

/

exit;

DECLARE
    v_rows SYS_REFCURSOR;
    v_row_index INTEGER;
    v_col_index INTEGER;
    v_value FLOAT;
BEGIN
    -- Call the function
    v_rows := graph_to_cursor(2, 10, 2, NULL);
    
    -- Fetch and print rows
    LOOP
        FETCH v_rows INTO v_row_index, v_col_index, v_value;
        EXIT WHEN v_rows%NOTFOUND;

        -- Output for testing (use DBMS_OUTPUT)
        DBMS_OUTPUT.PUT_LINE(
            'Row: ' || v_row_index ||
            ', Col: ' || v_col_index ||
            ', Val: ' || v_value
        );
    END LOOP;

    CLOSE v_rows;
END;
/
