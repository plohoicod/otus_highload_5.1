package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.otus.highload.homework.fourth.dto.GetMessagesDto;
import ru.otus.highload.homework.fourth.dto.MessageInDto;
import ru.otus.highload.homework.fourth.dto.MessageOutDto;
import ru.otus.highload.homework.fourth.dto.SendMessageDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    @Value("${message.serice.url}")
    private String messageServiceUrl;

    private final ChatService chatService;


    public void createMessage(SendMessageDto dto) {
        WebClient client = WebClient.create(messageServiceUrl);
        Long chatId = chatService.getChatId(dto.userFrom(), dto.userTo());
        client.post()
                .uri("/message/add")
                .bodyValue(new MessageInDto(chatId, dto.userFrom(), dto.userTo(), dto.message()))
                .retrieve()
                .bodyToMono(Void.class).block();
    }

    public List<SendMessageDto> getMessages(GetMessagesDto dto) {
        WebClient client = WebClient.create(messageServiceUrl);
        Long chatId = chatService.getChatId(dto.userFrom(), dto.userTo());

        Mono<MessageOutDto[]> response  = client.get()
                .uri("/messages/get/" + chatId)
                .retrieve().bodyToMono(MessageOutDto[].class).log();

        MessageOutDto[] messageOutDtos = response.block();
        if (messageOutDtos != null) {
            Arrays.sort(messageOutDtos, Comparator.comparing(MessageOutDto::timestamp));
            return Arrays.stream(messageOutDtos).map(m -> new SendMessageDto(m.from(), m.to(), m.message()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
