package ru.otus.highload.homework.fourth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.homework.fourth.service.ChatService;
import ru.otus.highload.homework.fourth.service.UserService;


@RestController
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;

    private final ChatService chatService;

    @GetMapping("/chat/get/with_user/{userId}")
    @Operation(summary = "Получение чата")
    public Long getFeed(@PathVariable Long userId) {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = userService.getUserIdByLogin(userDetails.getUsername());
        return chatService.getChatId(id, userId);
    }
}
