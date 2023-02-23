USE my_project;

CREATE table if not exists books
(
    id         BIGINT primary key auto_increment,
    name       varchar(50) NOT NULL,
    year       int DEFAULT 0,
    genre_book varchar(50) NOT NULL,
    amount int DEFAULT 0,
    price DOUBLE DEFAULT 0,
    author_id  BIGINT,
    FOREIGN KEY (author_id) REFERENCES authors (id)
);
