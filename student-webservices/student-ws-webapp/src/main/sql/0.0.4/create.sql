--
-- for security this must run as student-owner, not student-user!
--

--
-- create an idempotent stored procedure that creates the initial database schema.
--
create or replace function create_schema_0_0_4() returns void as $$
declare
    schema_version_rec record;
    schema_count int;
begin
    create table if not exists schema_version (
        schema_version varchar(20) not null
    );
    
    select count(*) into schema_count from schema_version;
    
    case schema_count
        when 0 then
            raise notice 'new table!';
            -- we just created table
            insert into schema_version(schema_version) values('0.0.4');
        when 1 then
            -- this is 'create' so we only need to make sure it's current version
            -- normally we accept either current version or immediately prior version.
            select * into strict schema_version_rec from schema_version;
            if schema_version_rec.schema_version <> '0.0.3' then
                raise notice 'Unwilling to run updates - check prior version';
                exit;
            end if;      
        else
            raise notice 'Bad database - more than one schema versions defined!';
            exit;
    end case;

    -- create tables!
    -- postgresql has a 'uuid' type but we're using varchar for portability.

    create table if not exists test_run (
        test_run_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        name varchar(80) not null,
        test_date timestamp not null,
        username varchar(40) not null
    );
    
    create table if not exists classroom (
        classroom_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        name varchar(80) not null
    );

    create table if not exists course (
        course_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        code varchar(12) not null unique,
        name varchar(80) not null,
        summary varchar(400),
        description text,
        credit_hours int
    );

    create table if not exists instructor (
        instructor_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        name varchar(80) not null,
        email varchar(200) unique not null
    );

    create table if not exists section (
        section_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        name varchar(80) not null
    );

    create table if not exists student (
        student_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        name varchar(80) not null,
        email varchar(200) unique not null
    );

    create table if not exists term (
        term_pkey serial primary key,
        version int not null default 1,
        uuid varchar(40) unique not null,
        creation_date timestamp not null,
        test_run_pkey int references test_run(test_run_pkey),
        name varchar(80) not null
    );

    -- make sure nobody can truncate our tables
    revoke truncate on classroom, course, instructor, section, student, term, test_run from public;
    revoke truncate on classroom, course, instructor, section, student, term, test_run from student;

    -- grant CRUD privileges to student-user.
    grant select, insert, update, delete on classroom, course, instructor, section, student, term, test_run to student;
    grant usage on classroom_classroom_pkey_seq to student;
    grant usage on course_course_pkey_seq to student;
    grant usage on instructor_instructor_pkey_seq to student;
    grant usage on section_section_pkey_seq to student;
    grant usage on student_student_pkey_seq to student;
    grant usage on term_term_pkey_seq to student;
    grant usage on test_run_test_run_pkey_seq to student;
    
    return;
end;
$$ language plpgsql;

-- create database schema
select create_schema_0_0_4() is null;

-- clean up
drop function create_schema_0_0_4();