package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.highload.homework.fourth.dto.AuthorizeDto;
import ru.otus.highload.homework.fourth.dto.RegisterDto;
import ru.otus.highload.homework.fourth.dto.UserDto;
import ru.otus.highload.homework.fourth.enums.Gender;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

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

    private final PasswordEncoder pwdEncoder;

    private final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?;";

    private final String FIND_USER_BY_LOGIN = "SELECT * FROM users WHERE login like ?;";

    private final String FIND_USER_BY_NAME_AND_SURNAME = "SELECT * FROM users WHERE name like ? and surname like ? order by id;";

    private final String REGISTER_USER =
            "INSERT INTO users (login, password, name, surname, birthday, gender, interests, city) VALUES (?,?, ?, ?, ?, ?, ?, ?) RETURNING id;";


    public AuthorizeDto getUserByLogin(String login) {
        AuthorizeDto authorizeDto = null;

        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_LOGIN);
            preparedStatement.setString(1, login);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                authorizeDto = new AuthorizeDto(
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return authorizeDto;
    }

    public Long getUserIdByLogin(String login) {
        Long id = null;

        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_LOGIN);
            preparedStatement.setString(1, login);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getLong("id");
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return id;
    }

    public List<UserDto> getUserByNameAndSurname(String name, String surname) {
        List<UserDto> userDtos = new ArrayList<>();
        if (name == null || name.isEmpty() || surname == null || surname.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try (Connection connection =
                     DriverManager.getConnection(urlSlave, userSlave, passwordSlave);) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_NAME_AND_SURNAME);
            preparedStatement.setString(1, name + "%");
            preparedStatement.setString(2, surname + "%");

            userDtos = getUsersDto(preparedStatement);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userDtos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return userDtos;
        }
    }

    private UserDto getUserDto(UserDto userDto, PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Calendar calendar = Calendar.getInstance();
            if (resultSet.getTimestamp("birthday") != null) {
                    calendar.setTimeInMillis(resultSet.getTimestamp("birthday").getTime());
            } else {
                    calendar = null;
            }
            userDto = new UserDto(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("surname"),
                    calendar,
                    resultSet.getString("gender") != null ? Gender.valueOf(resultSet.getString("gender")) : null,
                    resultSet.getString("interests"),
                    resultSet.getString("city")
            );
        }
        return userDto;
    }

    private List<UserDto> getUsersDto(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        List<UserDto> userDtos = new ArrayList<>();
        while (resultSet.next()) {
            Calendar calendar = Calendar.getInstance();
            if (resultSet.getTimestamp("birthday") != null) {
                calendar.setTimeInMillis(resultSet.getTimestamp("birthday").getTime());
            } else {
                calendar = null;
            }
            userDtos.add(new UserDto(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("surname"),
                    calendar,
                    resultSet.getString("gender") != null ? Gender.valueOf(resultSet.getString("gender")) : null,
                    resultSet.getString("interests"),
                    resultSet.getString("city")
            ));
        }
        return userDtos;
    }

    public UserDto getUserById(Long id) {
        UserDto userDto = null;
        try (Connection connection =
                     DriverManager.getConnection(urlSlave, userSlave, passwordSlave);) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID);
            preparedStatement.setLong(1, id);

            userDto = getUserDto(userDto, preparedStatement);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return userDto;
        }
    }

    public Long registerUser(RegisterDto registerDto) {
        if (getUserByLogin(registerDto.login()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
             String password = pwdEncoder.encode(registerDto.password());
             PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_USER);
             preparedStatement.setString(1, registerDto.login());
             preparedStatement.setString(2, password);
             preparedStatement.setString(3, registerDto.name());
             preparedStatement.setString(4, registerDto.surname());
             preparedStatement.setTimestamp(5, new Timestamp(registerDto.birthday().getTimeInMillis()));
             preparedStatement.setString(6, registerDto.gender().toString());
             preparedStatement.setString(7, registerDto.interests());
             preparedStatement.setString(8, registerDto.city());
             ResultSet resultSet = preparedStatement.executeQuery();

             if (resultSet.next()) {
                 return resultSet.getLong("id");
             } else {
                 return null;
             }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
