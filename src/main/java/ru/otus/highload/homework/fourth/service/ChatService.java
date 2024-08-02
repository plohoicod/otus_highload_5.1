package ru.otus.highload.homework.fourth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
@RequiredArgsConstructor
public class ChatService {
    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Value("${db.url.slave}")
    private String urlSlave;

    @Value("${db.user.slave}")
    private String userSlave;

    @Value("${db.password.slave}")
    private String passwordSlave;

    private final String GET_CHAT =
            "SELECT * FROM chats WHERE user_least = ? and user_greatest = ?;";


    private final String CREATE_CHAT =
            "INSERT INTO chats (user1_id, user2_id) VALUES (?, ?) RETURNING id;";


    public Long getChatId(Long user1Id, Long user2Id) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CHAT);

            if (user1Id < user2Id) {
                preparedStatement.setLong(1, user1Id);
                preparedStatement.setLong(2, user2Id);
            } else {
                preparedStatement.setLong(2, user1Id);
                preparedStatement.setLong(1, user2Id);
            }


            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("id");
            } else {
                PreparedStatement preparedStatement2 = connection.prepareStatement(CREATE_CHAT);
                if (user1Id < user2Id) {
                    preparedStatement2.setLong(1, user1Id);
                    preparedStatement2.setLong(2, user2Id);
                } else {
                    preparedStatement2.setLong(2, user1Id);
                    preparedStatement2.setLong(1, user2Id);
                }

                resultSet = preparedStatement2.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                } else {
                    System.out.println("Чат не создан");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
