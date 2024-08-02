package ru.otus.highload.homework.fourth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.highload.homework.fourth.dto.*;
import ru.otus.highload.homework.fourth.entity.RedisFeedEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {

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

    private final FriendService friendService;

    private final RedisService redisService;

    private final String ADD_POST =
            "INSERT INTO posts (author_user_id, text) VALUES (?,?);";

    private final String UPDATE_POST =
            "UPDATE posts SET text = ? WHERE id = ?;";

    private final String GET_POST =
            "SELECT * FROM posts WHERE id = ?;";

    private final String DELETE_POST =
            "DELETE FROM posts WHERE id = ?;";


    private final String GET_FEED =
            "SELECT * FROM posts WHERE author_user_id = ANY (?) ORDER BY create_datetime LIMIT ? OFFSET ?;";

    public void addPost(CreatePostDto dto) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_POST);
            preparedStatement.setLong(1, dto.userId());
            preparedStatement.setString(2, dto.text());
            preparedStatement.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updatePost(UpdatePostDto dto) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST);
            preparedStatement.setString(1, dto.text());
            preparedStatement.setLong(2, dto.postId());
            preparedStatement.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deletePost(Long id) {
        try (Connection connection =
                     DriverManager.getConnection(url, user, password);) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POST);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PostDto getPostById(Long id) {
        PostDto postDto = null;
        try (Connection connection =
                     DriverManager.getConnection(urlSlave, userSlave, passwordSlave);) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_POST);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                postDto = new PostDto(
                        resultSet.getLong("id"),
                        resultSet.getLong("author_user_id"),
                        resultSet.getString("text")
                );
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (postDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return postDto;
        }
    }

    public List<PostDto> getFeed(Long offset, Long limit, Long userId) {
        if (offset == null || limit == null || userId == null ||
                offset > limit || limit > 1000 || offset < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<PostDto> posts = new ArrayList<>();

        RedisFeedEntity redisFeedEntity = redisService.getById(userId);

        if (redisFeedEntity != null) {
            return redisFeedEntity.getPostList().subList(offset.intValue(), limit.intValue());
        }
        try (Connection connection =
                     DriverManager.getConnection(urlSlave, userSlave, passwordSlave);) {
            List<Long> friendsList = friendService.getFriedsIdList(userId);


            if (friendsList != null && !friendsList.isEmpty()) {
                PreparedStatement preparedStatement = connection.prepareStatement(GET_FEED);
                Array array = connection.createArrayOf("bigint", friendsList.stream().map(String::valueOf).toArray(String[]::new));
                preparedStatement.setArray(1, array);
                preparedStatement.setLong(2, 1000);
                preparedStatement.setLong(3, 0);

                System.out.println(preparedStatement);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    posts.add(new PostDto(
                            resultSet.getLong("id"),
                            resultSet.getLong("author_user_id"),
                            resultSet.getString("text")
                    ));
                }
            }

            redisService.save(posts, userId);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return posts.subList(offset.intValue(), limit.intValue());
    }
}
