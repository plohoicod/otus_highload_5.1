package ru.otus.highload.homework.fourth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.highload.homework.fourth.entity.RedisFeedEntity;

@Repository
public interface RedisFeedRepository extends CrudRepository<RedisFeedEntity, String> {
}
