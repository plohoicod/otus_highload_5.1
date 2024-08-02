package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.highload.homework.fourth.dto.CreatePostDto;
import ru.otus.highload.homework.fourth.dto.FriendDto;
import ru.otus.highload.homework.fourth.dto.RegisterDto;
import ru.otus.highload.homework.fourth.enums.Gender;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrepareDataService {
    public final UserService userService;
    public final FriendService friendService;
    public final PostsService postsService;


    //@EventListener(ApplicationReadyEvent.class)
    public void prepareData() throws IOException {
        String user1 = "otus_dz4_user1";
        String user2 = "otus_dz4_user2";
        String user3 = "otus_dz4_user3";
        FileInputStream file = new FileInputStream("/Users/ydpolivt/IdeaProjects/otus_highload_3/posts.txt");

        List<String> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        Long userId1 = userService.registerUser(new RegisterDto(user1, user1, user1, user1, cal, Gender.MALE, user1, user1));
        Long userId2 = userService.registerUser(new RegisterDto(user2, user2, user2, user2, cal, Gender.MALE, user2, user2));
        Long userId3 = userService.registerUser(new RegisterDto(user3, user3, user3, user3, cal, Gender.MALE, user3, user3));




        friendService.addFriend(new FriendDto(userId1, userId2));
        friendService.addFriend(new FriendDto(userId1, userId3));


        for (int i = 0; i < 1000; i++) {
            postsService.addPost(new CreatePostDto(userId2, list.get(i)));
        }

        for (int i = 1000; i < 2000; i++) {
            postsService.addPost(new CreatePostDto(userId3, list.get(i)));
        }


    }
}
