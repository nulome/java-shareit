package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    private static final String SELECT_ALL_ITEMS_SQL = "SELECT i.id AS item_id, i.name AS item_name, i.description, " +
            "i.available, i.owner_id FROM items i ";
    private static final String INSERT_ITEM_SQL = "INSERT INTO items (name, description, available, owner_id) " +
            "VALUES (?, ?, ?, ?)";
    private static final String SELECT_ITEMS_BY_USER_SQL = SELECT_ALL_ITEMS_SQL + "WHERE i.owner_id = ? ";
    private static final String UPDATE_ITEM_SQL = "UPDATE items SET name = ?, description = ?, available = ? " +
            "WHERE id = ? ";
    private static final String DELETE_ITEM_BY_ID_SQL = "DELETE FROM items WHERE id = ? ";
    private static final String SELECT_ITEM_BY_ID_SQL = SELECT_ALL_ITEMS_SQL + "WHERE i.id = ? ";
    private static final String SELECT_ITEM_BY_TEXT_SEARCH_SQL = "SELECT i.id AS item_id, i.name AS item_name, " +
            "i.description, i.available, i.owner_id, u.id AS user_id, u.name AS user_name, u.email FROM items i " +
            "LEFT JOIN users u ON i.owner_id = u.id " +
            "WHERE i.name ILIKE ? OR i.description ILIKE ? AND i.available ILIKE 't%' ";

    @Override
    public Item createItem(Item item) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_ITEM_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBoolean(3, item.getAvailable());
            ps.setInt(4, item.getOwner().getId());
            return ps;
        }, keyHolder);
        item.setId(keyHolder.getKey().intValue());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        boolean bool = item.getAvailable();
        jdbcTemplate.update(UPDATE_ITEM_SQL, item.getName(), item.getDescription(), item.getAvailable(), item.getId());
        Item item1 = getItem(item.getId());
        return item1;
    }

    @Override
    public void deleteItem(Integer itemId) {
        jdbcTemplate.update(DELETE_ITEM_BY_ID_SQL, itemId);
    }

    @Override
    public List<Item> getItems(User user) {
        return jdbcTemplate.query(SELECT_ITEMS_BY_USER_SQL, (rs, rowNum) -> {
            Item itemResultSet = ItemRepository.createItemBuilder(rs);
            itemResultSet.setOwner(user);
            return itemResultSet;
        }, user.getId());
    }

    @Override
    public Item getItem(Integer itemId) {
        return jdbcTemplate.queryForObject(SELECT_ITEM_BY_ID_SQL, (rs, rowNum) -> {
            Item itemResultSet = ItemRepository.createItemBuilder(rs);
            itemResultSet.setOwner(userRepository.getUser(rs.getInt("owner_id")));
            return itemResultSet;
        }, itemId);
    }

    @Override
    public List<Item> getItemByTextSearch(String text) {
        return jdbcTemplate.query(SELECT_ITEM_BY_TEXT_SEARCH_SQL, (rs, rowNum) -> {
            Item itemResultSet = ItemRepository.createItemBuilder(rs);
            itemResultSet.setOwner(UserRepository.createUserBuilder(rs));
            return itemResultSet;
        }, getSearchParam(text), getSearchParam(text));
    }

    private String getSearchParam(String text) {
        return "%" + text + "%";
    }
}
