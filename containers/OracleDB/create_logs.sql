CREATE TABLE ddl_logs (
    log_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username       VARCHAR2(100),
    os_user        VARCHAR2(100),
    host           VARCHAR2(100),
    ip_address     VARCHAR2(100),
    action         VARCHAR2(100),
    object_name    VARCHAR2(100),
    object_type    VARCHAR2(100),
    ddl_text       CLOB,
    log_time       TIMESTAMP DEFAULT SYSTIMESTAMP
);

/

CREATE OR REPLACE TRIGGER prevent_table_modification
AFTER CREATE OR ALTER OR DROP ON DATABASE
DECLARE
    v_sql_text   CLOB;
BEGIN
    IF ora_dict_obj_type = 'TABLE' THEN
        SELECT sys_context('USERENV', 'SESSION_USER'),
               sys_context('USERENV', 'OS_USER'),
               sys_context('USERENV', 'HOST'),
               sys_context('USERENV', 'IP_ADDRESS')
        INTO   :NEW.username,
               :NEW.os_user,
               :NEW.host,
               :NEW.ip_address
        FROM   dual;

        INSERT INTO ddl_logs (
            username,
            os_user,
            host,
            ip_address,
            action,
            object_name,
            object_type,
            ddl_text
        )
        VALUES (
            sys_context('USERENV', 'SESSION_USER'),
            sys_context('USERENV', 'OS_USER'),
            sys_context('USERENV', 'HOST'),
            sys_context('USERENV', 'IP_ADDRESS'),
            ora_sysevent,
            ora_dict_obj_name,
            ora_dict_obj_type,
            DBMS_STANDARD.DIAG_SQL_TEXT
        );
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20000, 'DDL operations on tables are not allowed in this database.');
    END IF;
END;
/

commit;

exit;