package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);

    List<User> getUsers();

    User getUser(Integer id);

    static User createUserBuilder(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .build();
    }
}
