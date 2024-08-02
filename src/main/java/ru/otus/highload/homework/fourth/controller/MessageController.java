package ru.otus.highload.homework.fourth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.homework.fourth.dto.GetMessagesDto;
import ru.otus.highload.homework.fourth.dto.SendMessageDto;
import ru.otus.highload.homework.fourth.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/dialog/send")
    @Operation(summary = "Создать сообщение")
    public void createMessage(@RequestBody SendMessageDto sendMessageDto) {
        messageService.createMessage(sendMessageDto);
    }

    @GetMapping("/dialog/list")
    @Operation(summary = "Получить сообщение")
    public List<SendMessageDto> getMessages(@ParameterObject GetMessagesDto dto) {
        return messageService.getMessages(dto);
    }

}
