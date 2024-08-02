package ru.otus.highload.homework.fourth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.homework.fourth.dto.GetFeedDto;
import ru.otus.highload.homework.fourth.dto.PostDto;
import ru.otus.highload.homework.fourth.service.PostsService;
import ru.otus.highload.homework.fourth.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostsService postsService;

    private final UserService userService;

    @GetMapping("/post/feed")
    @Operation(summary = "Получение ленты")
    public List<PostDto> getFeed(@ParameterObject GetFeedDto dto) {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = userService.getUserIdByLogin(userDetails.getUsername());
        return postsService.getFeed(dto.offset(), dto.limit(), id);
    }
}
