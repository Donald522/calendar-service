create table if not exists users (
  id integer not null,
  name varchar(255) not null,
  surname varchar(255) not null,
  email varchar(255) not null,
  constraint user_pk primary key (id)
);

create sequence if not exists users_seq;