package ru.otus.highload.homework.fourth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Date;

public record MessageOutDto(String requestId, Long from, Long to, String message, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:SS")Date timestamp) {
}
