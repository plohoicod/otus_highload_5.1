package ru.otus.highload.homework.fourth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.highload.homework.fourth.dto.PostDto;
import ru.otus.highload.homework.fourth.entity.RedisFeedEntity;
import ru.otus.highload.homework.fourth.repository.RedisFeedRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisFeedRepository repository;

    @Value("${redis.time-to-live}")
    private Long timeToLive;

    public void save(List<PostDto> list, Long userId) {
        repository.save(new RedisFeedEntity(userId.toString(), list, timeToLive));
    }

    public RedisFeedEntity getById(Long userId) {
        return repository.findById(userId.toString()).orElse(null);
    }
}
