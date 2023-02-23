USE my_project;

CREATE table if not exists item_book(
    item_id bigint REFERENCES items(id),
    book_id bigint REFERENCES books(id),
    PRIMARY KEY (item_id,book_id)
)