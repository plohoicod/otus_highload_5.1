create table if not exists friends
(
    user_id bigint REFERENCES users(id) NOT NULL,
    friend_id bigint REFERENCES users(id) NOT NULL,
    PRIMARY KEY (user_id, friend_id)
    );