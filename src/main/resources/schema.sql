create table if not exists users (
  id integer not null,
  name varchar(255) not null,
  surname varchar(255) not null,
  email varchar(255) not null,
  constraint user_pk primary key (id)
);

create table if not exists calendar (
  meeting_id varchar(255) not null,
  user_email varchar(255) not null,
  meeting_title varchar(255),
  from_time timestamp not null,
  to_time timestamp not null,
  response varchar(32) not null default 'TENTATIVE'
);

create table if not exists meetings (
  id bigint not null primary key,
  meeting_title varchar(255),
  from_time timestamp not null,
  to_time timestamp not null,
  location varchar(255),
  organizer varchar(255) not null,
  message clob
);

create sequence if not exists users_seq;

create sequence if not exists meetings_seq;