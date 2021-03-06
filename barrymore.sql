
SET SQL DIALECT 3; 

/* CREATE DATABASE 'localhost:barrymore' PAGE_SIZE 16384 DEFAULT CHARACTER SET UNICODE_FSS; */


/*  Generators or sequences */
CREATE GENERATOR GEN_ALIAS_ID START WITH 21;
CREATE GENERATOR GEN_ENTITY_ID START WITH 21;

COMMIT WORK;

/* Table: ACTION, Owner: SYSDBA */
CREATE TABLE ACTION (ID INTEGER NOT NULL,
        KCLASS INTEGER NOT NULL,
CONSTRAINT PK_ACTION PRIMARY KEY (ID, KCLASS));

/* Table: ALIAS, Owner: SYSDBA */
CREATE TABLE ALIAS (ID INTEGER NOT NULL,
        KENTITY INTEGER,
        PWORD VARCHAR(64),
CONSTRAINT ALIAS_PK PRIMARY KEY (ID));

/* Table: CLASS, Owner: SYSDBA */
CREATE TABLE CLASS (ID INTEGER NOT NULL,
CONSTRAINT CLASS_PK PRIMARY KEY (ID));

/* Table: ENTITY, Owner: SYSDBA */
CREATE TABLE ENTITY (ID INTEGER NOT NULL,
        PNAME VARCHAR(32),
        PTYPE CHAR(1) NOT NULL,
CONSTRAINT ENTITY_PK PRIMARY KEY (ID));

/* Table: LOCATION, Owner: SYSDBA */
CREATE TABLE LOCATION (ID INTEGER NOT NULL,
        SX INTEGER,
        SY INTEGER,
        SZ INTEGER,
        EX INTEGER,
        EY INTEGER,
        EZ INTEGER,
CONSTRAINT PK_LOCATION PRIMARY KEY (ID));

/* Table: OBJECT, Owner: SYSDBA */
CREATE TABLE OBJECT (ID INTEGER NOT NULL,
        KLOCATION INTEGER,
        KCLASS INTEGER,
        ADDRESS VARCHAR(64) NOT NULL,
CONSTRAINT OBJECT_PK PRIMARY KEY (ID));

/* Table: TEST, Owner: SYSDBA */
CREATE TABLE TEST (FOO CHAR(3));

ALTER TABLE ACTION ADD CONSTRAINT FK_ACTION_ENTITY FOREIGN KEY (ID) REFERENCES ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ALIAS ADD CONSTRAINT ALIAS_ENTITY_ID_FK FOREIGN KEY (KENTITY) REFERENCES ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE CLASS ADD CONSTRAINT FK_CLASS_1 FOREIGN KEY (ID) REFERENCES ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE LOCATION ADD CONSTRAINT FK_LOCATION_1 FOREIGN KEY (ID) REFERENCES ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE OBJECT ADD CONSTRAINT FK_OBJECT_1 FOREIGN KEY (KLOCATION) REFERENCES LOCATION (ID) ON UPDATE SET NULL ON DELETE SET NULL;

ALTER TABLE OBJECT ADD CONSTRAINT FK_OBJECT_2 FOREIGN KEY (ID) REFERENCES ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE OBJECT ADD CONSTRAINT OBJECT_CLASS_ID_FK FOREIGN KEY (KCLASS) REFERENCES CLASS (ID) ON UPDATE CASCADE ON DELETE CASCADE;

/* View: V_ACTION, Owner: SYSDBA */
CREATE VIEW V_ACTION (ID, PNAME, KCLASS) AS
select a.ID, e.pName, a.kClass from Action a
    left join Entity e on a.ID = e.ID;

/* View: V_CLASS, Owner: SYSDBA */
CREATE VIEW V_CLASS (ID, PNAME) AS
select c.ID, e.pName from Class c
    left join Entity e on c.ID = e.ID;

/* View: V_LOCATION, Owner: SYSDBA */
CREATE VIEW V_LOCATION (ID, PNAME, SX, SY, SZ, EX, EY, EZ) AS
SELECT l.ID, e.PNAME, l.SX, l.SY, l.SZ, l.EX, l.EY, l.EZ FROM LOCATION l LEFT JOIN ENTITY E on l.ID = E.ID;

/* View: V_OBJECT, Owner: SYSDBA */
CREATE VIEW V_OBJECT (ID, PNAME, KLOCATION, KCLASS, ADDRESS) AS
select o.ID, e.pName, o.kLocation, o.kClass, o.ADDRESS from Object o
    left join Entity e on o.ID = e.ID;

/* Table constraints */

ALTER TABLE ENTITY ADD 
        CONSTRAINT CHK1_ENTITY CHECK (pType in ('A','C','L','O')
);

ALTER TABLE LOCATION ADD 
        CHECK (EX >= SX AND EY >= SY AND EZ >= SZ);
SET TERM ^ ;

/* Triggers only will work for SQL triggers */
CREATE TRIGGER T_ALIAS_BI FOR ALIAS 
ACTIVE BEFORE INSERT POSITION 0 
AS BEGIN
if (New.ID is null) then New.ID = Gen_ID(GEN_ALIAS_ID, 1);
END ^

CREATE TRIGGER TV_ACTION_BI FOR V_ACTION 
ACTIVE BEFORE INSERT POSITION 0 
as
begin
  if (New.ID is null) then begin
    New.ID = Gen_ID(GEN_ENTITY_ID, 1);
    insert into Entity (ID, PNAME, PTYPE) values (NEW.ID, NEW.PNAME, 'A');
  end
  insert into Action (ID, kClass) values (NEW.ID, NEW.kClass);
end ^

CREATE TRIGGER TV_ACTION_BU FOR V_ACTION 
ACTIVE BEFORE UPDATE POSITION 0 
as
begin
  update Action set kClass=NEW.kClass where ID=OLD.ID;
  update Entity set pName=NEW.pName where ID=OLD.ID;
end ^

CREATE TRIGGER TV_ACTION_BD FOR V_ACTION 
ACTIVE BEFORE DELETE POSITION 0 
as
begin
  delete from Entity where ID=OLD.ID;
end ^

CREATE TRIGGER TV_CLASS_BI FOR V_CLASS 
ACTIVE BEFORE INSERT POSITION 0 
as
begin
  if (New.ID is null) then New.ID = Gen_ID(GEN_ENTITY_ID, 1);
  insert into Entity (ID, PNAME, PTYPE) values (NEW.ID, NEW.PNAME, 'C');
  insert into Class (ID) values (NEW.ID);
end ^

CREATE TRIGGER TV_CLASS_BU FOR V_CLASS 
ACTIVE BEFORE UPDATE POSITION 0 
as
begin
  update Entity set pName=NEW.pName where ID=OLD.ID;
end ^

CREATE TRIGGER TV_CLASS_BD FOR V_CLASS 
ACTIVE BEFORE DELETE POSITION 0 
as
begin
  delete from Entity where ID=OLD.ID;
end ^

CREATE TRIGGER TV_LOCATION_BI FOR V_LOCATION 
ACTIVE BEFORE INSERT POSITION 0 
AS
begin
  if (New.ID is null) then New.ID = Gen_ID(GEN_ENTITY_ID, 1);
  insert into Entity (ID, PNAME, PTYPE) values (NEW.ID, NEW.PNAME, 'L');
insert into Location(ID, SX, SY, SZ, EX, EY, EZ) VALUES (NEW.ID, NEW.SX, NEW.SY, NEW.SZ, NEW.EX, NEW.EY, NEW.EZ);
end ^

CREATE TRIGGER TV_LOCATION_BU FOR V_LOCATION 
ACTIVE BEFORE UPDATE POSITION 0 
AS
begin
update Entity set pName=NEW.pName where ID=OLD.ID;
update Location set SX=NEW.SX, SY=NEW.SY, SZ=NEW.SZ, EX=NEW.EX, EY=NEW.EY, EZ=NEW.EZ where ID=OLD.ID;
end ^

CREATE TRIGGER TV_LOCATION_BD FOR V_LOCATION 
ACTIVE BEFORE DELETE POSITION 0 
AS
begin
  delete from Entity where ID=OLD.ID;
end ^

CREATE TRIGGER TV_OBJECT_BI FOR V_OBJECT 
ACTIVE BEFORE INSERT POSITION 0 
AS
begin
  if (New.ID is null) then New.ID = Gen_ID(GEN_ENTITY_ID, 1);
  insert into Entity (ID, PNAME, PTYPE) values (NEW.ID, NEW.PNAME, 'L');
insert into Object (ID, kLocation, kClass, address) values (NEW.ID, NEW.kLocation, NEW.kClass, NEW.address);
end ^

CREATE TRIGGER TV_OBJECT_BU FOR V_OBJECT 
ACTIVE BEFORE UPDATE POSITION 0 
as
begin
  update Entity set pName=NEW.pName where ID=OLD.ID;
update Object set kLocation=NEW.kLocation, kClass=NEW.kClass, address=NEW.address where ID=OLD.ID;
end ^

CREATE TRIGGER TV_OBJECT_BD FOR V_OBJECT 
ACTIVE BEFORE DELETE POSITION 0 
AS
BEGIN
  delete from Entity where ID=OLD.ID;
end ^


SET TERM ; ^
COMMIT WORK;

/* Grant permissions for this database */
GRANT SELECT ON ACTION TO USER BARRYMORE;
GRANT SELECT ON ALIAS TO USER BARRYMORE;
GRANT SELECT ON CLASS TO USER BARRYMORE;
GRANT SELECT ON ENTITY TO USER BARRYMORE;
GRANT SELECT ON LOCATION TO USER BARRYMORE;
GRANT SELECT ON OBJECT TO USER BARRYMORE;
GRANT SELECT ON TEST TO USER BARRYMORE;
GRANT SELECT ON V_ACTION TO USER BARRYMORE;
GRANT SELECT ON V_CLASS TO USER BARRYMORE;
GRANT SELECT ON V_LOCATION TO USER BARRYMORE;
GRANT SELECT ON V_OBJECT TO USER BARRYMORE;

/* Comments for database objects. */
COMMENT ON TABLE        ENTITY IS 'Сущность, нечто именуемое';
