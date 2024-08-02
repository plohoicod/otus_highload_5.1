package ru.otus.highload.homework.fourth.dto;


public record SendMessageDto(Long userFrom, Long userTo, String message) {
}
