package ru.otus.highload.homework.fourth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import ru.otus.highload.homework.fourth.dto.PostDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RedisHash(value = "user_feed")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RedisFeedEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5067980164721834671L;

    @Id
    private String userId;


    private List<PostDto> postList;

    @TimeToLive
    private Long ttl;
}
