CREATE TRIGGER prevent_table_creation
ON DATABASE
FOR CREATE_TABLE, ALTER_TABLE, DROP_TABLE
AS 
BEGIN
   PRINT 'you can not create, drop and alter tables in this database';
   ROLLBACK;
END;

CREATE TRIGGER no_more_than_50_tags
BEFORE INSERT
ON USER_TAG_FRAMES
AS
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM NEW;
    IF v_count > 50 THEN
        
    END IF;
END;