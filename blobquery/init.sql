CREATE USER myuser IDENTIFIED BY mypassword;
GRANT CONNECT, RESOURCE TO myuser;
ALTER USER myuser DEFAULT TABLESPACE users;

CREATE TABLE myuser.MY_TABLE (
  ID NUMBER(10),
  CREATED TIMESTAMP,
  DATA BLOB
);

BEGIN
  FOR i IN 1..10 LOOP
    INSERT INTO myuser.MY_TABLE (ID, CREATED, DATA) VALUES (i, CURRENT_TIMESTAMP, EMPTY_BLOB());

    DECLARE
      l_blob BLOB;
    BEGIN
      SELECT DATA INTO l_blob FROM myuser.MY_TABLE WHERE ID = i AND ROWNUM = 1 FOR UPDATE;
      DBMS_LOB.CREATETEMPORARY(l_blob, TRUE);
      DBMS_LOB.OPEN(l_blob, DBMS_LOB.LOB_READWRITE);
      DBMS_LOB.WRITE(l_blob, LENGTH('test data')+3, 1, UTL_RAW.CAST_TO_RAW('test data ' || i || '  '));
      DBMS_LOB.CLOSE(l_blob);
      UPDATE myuser.MY_TABLE SET DATA = l_blob WHERE ID = i;
      COMMIT;
    END;
  END LOOP;
END;
/

INSERT INTO myuser.MY_TABLE (ID, CREATED, DATA) VALUES (11, CURRENT_TIMESTAMP, UTL_RAW.CAST_TO_RAW('test data 11'));
