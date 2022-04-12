create table if not exists users (
  email varchar(255) primary key,
  name varchar(255) not null,
  surname varchar(255) not null
);