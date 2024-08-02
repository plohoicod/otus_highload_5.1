package ru.otus.highload.homework.fourth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.homework.fourth.dto.FriendDto;
import ru.otus.highload.homework.fourth.service.FriendService;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    @PostMapping("/friend/set")
    @Operation(summary = "Добавление друга")
    public void registerUser(@RequestBody FriendDto dto) {
        friendService.addFriend(dto);
    }

    @DeleteMapping ("/friend/delete")
    @Operation(summary = "Удаление друга")
    public void deleteFriend(@RequestBody FriendDto dto) {
        friendService.deleteFriend(dto);
    }
}
