package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.highload.homework.fourth.dto.FriendDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

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


    private final String ADD_FRIEND =
            "INSERT INTO friends (user_id, friend_id) VALUES (?,?);";

    private final String DELETE_FRIEND =
            "DELETE FROM friends WHERE user_id = ? and friend_id = ?;";

    private final String GET_FRIENDS =
            "SELECT * FROM friends WHERE user_id = ?;";

    public void addFriend(FriendDto dto) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_FRIEND);
            preparedStatement.setLong(1, dto.userId());
            preparedStatement.setLong(2, dto.friendId());
            preparedStatement.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteFriend(FriendDto dto) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND);
            preparedStatement.setLong(1, dto.userId());
            preparedStatement.setLong(2, dto.friendId());
            preparedStatement.executeQuery();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Long> getFriedsIdList(Long userId) {
        List<Long> friendsIds = new ArrayList<>();
        try (Connection connection =
                     DriverManager.getConnection(urlSlave, userSlave, passwordSlave);) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_FRIENDS);
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                friendsIds.add(resultSet.getLong("friend_id"));

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return friendsIds;
    }


}
