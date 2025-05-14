CREATE OR REPLACE PACKAGE tag_exceptions
AS
    over_50_user_tags EXCEPTION;
    PRAGMA EXCEPTION_INIT(student_not_found, -20001);
END exceptii;