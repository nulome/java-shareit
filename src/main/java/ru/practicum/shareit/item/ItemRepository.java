package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Integer itemId);

    List<Item> getItems(User user);

    Item getItem(Integer itemId);

    List<Item> getItemByTextSearch(String text);

    static Item createItemBuilder(ResultSet rs) throws SQLException {
        return Item.builder()
                .id(rs.getInt("item_id"))
                .name(rs.getString("item_name"))
                .description(rs.getString("description"))
                .available(rs.getBoolean("available"))
                .build();
    }
}
