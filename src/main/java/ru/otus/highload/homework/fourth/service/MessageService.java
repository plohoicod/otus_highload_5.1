package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.otus.highload.homework.fourth.dto.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    @Value("${message.serice.url}")
    private String messageServiceUrl;

    private final ChatService chatService;


    public void createMessage(SendMessageDto dto) {
        String uuid = UUID.randomUUID().toString();
        log.info("Sending request with uuid: {}", uuid);
        WebClient client = WebClient.create(messageServiceUrl);
        Long chatId = chatService.getChatId(dto.userFrom(), dto.userTo());
        CreateMessageAnswerDto createMessageAnswerDto = client.post()
                .uri("/message/add")
                .bodyValue(new MessageInDto(uuid, chatId, dto.userFrom(), dto.userTo(), dto.message()))
                .retrieve()
                .bodyToMono(CreateMessageAnswerDto.class).block();
        assert createMessageAnswerDto != null;
        log.info("Getting answer on request with uuid: {}", createMessageAnswerDto.requestId());
    }

    public List<SendMessageDto> getMessages(GetMessagesDto dto) {
        String uuid = UUID.randomUUID().toString();
        log.info("Sending request with uuid: {}", uuid);
        WebClient client = WebClient.create(messageServiceUrl);
        Long chatId = chatService.getChatId(dto.userFrom(), dto.userTo());

        Mono<MessageOutDto[]> response  = client.get()

                .uri(uriBuilder -> uriBuilder.path("/messages/get/" + chatId)
                        .queryParam("requestId", uuid).build())
                .retrieve().bodyToMono(MessageOutDto[].class).log();

        MessageOutDto[] messageOutDtos = response.block();
        if (messageOutDtos != null) {
            Arrays.sort(messageOutDtos, Comparator.comparing(MessageOutDto::timestamp));
            if (messageOutDtos[0] != null && messageOutDtos[0].requestId() != null)
                log.info("Getting answer on request with uuid: {}", messageOutDtos[0].requestId());
            return Arrays.stream(messageOutDtos).map(m -> new SendMessageDto(m.from(), m.to(), m.message()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
