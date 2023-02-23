USE my_project;

CREATE table if not exists items
(
    id             BIGINT primary key auto_increment,
    date_create    datetime    NOT NULL,
    date_progress  datetime,
    date_change    datetime,
    date_execute   datetime,
    date_cancelled datetime,
    state          varchar(50) NOT NULL,
    price double default 0,
    user_id        BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
