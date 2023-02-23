USE my_project;

CREATE table if not exists authors
(
    id      BIGINT primary key auto_increment,
    name    varchar(50) NOT NULL ,
    surname varchar(50) NOT NULL ,
    birth_year int DEFAULT 0,
    death_year int DEFAULT 0
);
