create table if not exists posts
(
    id bigserial not null,
    author_user_id bigint REFERENCES users(id) NOT NULL,
    text text,
    create_datetime timestamp default CURRENT_TIMESTAMP not null,
    primary key (id)
    );