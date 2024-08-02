package ru.otus.highload.homework.fourth.dto;

public record MessageInDto(Long chatId, Long userFromId, Long userToId, String message) {
}
