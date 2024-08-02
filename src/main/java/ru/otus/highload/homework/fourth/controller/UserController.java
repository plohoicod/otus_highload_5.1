package ru.otus.highload.homework.fourth.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.homework.fourth.dto.RegisterDto;
import ru.otus.highload.homework.fourth.dto.UserDto;
import ru.otus.highload.homework.fourth.dto.UserSearchDto;
import ru.otus.highload.homework.fourth.service.PostsService;
import ru.otus.highload.homework.fourth.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    private final PostsService postsService;


    @GetMapping("/user/get/{id}")
    @Operation(summary = "Получение анкеты пользователя")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/user/register")
    @Operation(summary = "Регистрация нового пользователя")
    public Long registerUser(@RequestBody RegisterDto registerDto) {
        return userService.registerUser(registerDto);
    }


    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public void login() {

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = userService.getUserIdByLogin(userDetails.getUsername());

        postsService.getFeed(0L, 1000L, id);

    }

    @GetMapping("/user/search")
    @Operation(summary = "Поиск анкеты пользователя")
    public List<UserDto> userSearch(@ParameterObject UserSearchDto dto) {
        return userService.getUserByNameAndSurname(dto.firstName(), dto.lastName());
    }
}
