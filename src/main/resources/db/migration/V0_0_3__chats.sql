create table chats(
                      id bigserial not null,
                      user1_id bigint,
                      user2_id bigint,
                      user_least    bigint GENERATED ALWAYS as (least(user1_id, user2_id))    stored,
                      user_greatest bigint GENERATED ALWAYS as (greatest(user1_id, user2_id)) stored,
                      unique (user_least, user_greatest)
);
