create table if not exists users (
  id integer not null,
  name varchar(255) not null,
  surname varchar(255) not null,
  email varchar(255) not null,
  password varchar(255) not null,
  constraint user_pk primary key (id)
);

create table if not exists calendar (
  meeting_id bigint not null,
  meeting_sub_id bigint not null default -1,
  user_email varchar(255) not null,
  response varchar(32) not null default 'TENTATIVE',
  constraint calendar_pk primary key (meeting_id, meeting_sub_id, user_email, response)
);

create table if not exists meetings (
  id bigint not null,
  sub_id bigint not null,
  meeting_title varchar(255),
  from_time timestamp not null,
  to_time timestamp not null,
  location varchar(255),
  organizer varchar(255) not null,
  visibility varchar(255) not null default 'PUBLIC',
  recurrence varchar(255) not null default 'NONE',
  message clob,
  constraint meeting_pk primary key (id, sub_id)
);

create sequence if not exists users_seq;

create sequence if not exists meetings_seq;