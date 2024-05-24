package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_USER_SQL = "INSERT INTO users (name, email) VALUES (?, ?)";
    private static final String SELECT_USERS_SQL = "SELECT u.id AS user_id, u.name AS user_name, u.email FROM users u ";
    private static final String UPDATE_USER_SQL = "UPDATE users u SET u.name = ?, u.email = ? WHERE id = ? ";
    private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM users WHERE id = ? ";
    private static final String SELECT_USER_BY_ID_SQL = SELECT_USERS_SQL + "WHERE u.id = ? ";
    private static final String SELECT_USER_CHECK_EMAIL_SQL = "SELECT u.id AS user_id FROM users u WHERE u.email = ? ";

    @Override
    public User createUser(User user) {
        checkUniqueEmailInDataBase(user, null);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUniqueEmailInDataBase(user, user.getId());
        jdbcTemplate.update(UPDATE_USER_SQL, user.getName(), user.getEmail(), user.getId());
        return getUser(user.getId());
    }

    @Override
    public void deleteUser(Integer userId) {
        jdbcTemplate.update(DELETE_USER_BY_ID_SQL, userId);
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(SELECT_USERS_SQL, userRowMapper());
    }

    @Override
    public User getUser(Integer id) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ID_SQL, userRowMapper(), id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> UserRepository.createUserBuilder(rs);
    }

    private void checkUniqueEmailInDataBase(User user, Integer checkId) {
        List<Integer> userIds = jdbcTemplate.query(SELECT_USER_CHECK_EMAIL_SQL,
                (rs, rowNum) -> rs.getInt("user_id"), user.getEmail());

        if (!userIds.isEmpty() && !Objects.equals(userIds.get(0), checkId)) {
            throw new DuplicateKeyException("Конфликт. Значение email: " + user.getEmail() + " уже используется.");
        }
    }
}
