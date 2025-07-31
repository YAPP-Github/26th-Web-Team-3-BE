-- Rename LETTER table to letter (lowercase)
drop table letter;

rename table LETTER to letter;

-- Rename LETTER_FILE table to letter_file (lowercase)
rename table LETTER_FILE to letter_file;

-- Rename columns in letter table to lowercase
alter table letter change column ID id bigint not null auto_increment;
alter table letter change column USER_ID user_id bigint;
alter table letter change column TIME_CAPSULE_ID time_capsule_id bigint;
alter table letter change column CONTENT content varchar(255);
alter table letter change column FROMS froms varchar(255);
alter table letter change column CREATED_AT created_at datetime(6) not null;
alter table letter change column UPDATED_AT updated_at datetime(6) not null;

-- Rename columns in letter_file table to lowercase
alter table letter_file change column ID id bigint not null auto_increment;
alter table letter_file change column LETTER_ID letter_id bigint;
alter table letter_file change column OBJECT_KEY object_key varchar(255) not null;
alter table letter_file change column CREATED_AT created_at datetime(6) not null;
alter table letter_file change column UPDATED_AT updated_at datetime(6) not null;
