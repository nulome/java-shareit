package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final EasyRandom random = new EasyRandom();

    private Item item;
    private final int userId1 = 1;
    private final int userId2 = 2;


    @BeforeAll
    void setUp() {
        User user1 = random.nextObject(User.class);
        user1.setId(userId1);
        userRepository.save(user1);
        item = new Item(1, "NaMeSSearch", "DescriPtion", true, user1, null, null);
        itemRepository.save(item);

        User user2 = random.nextObject(User.class);
        user2.setId(userId2);
        userRepository.save(user2);
        item = new Item(2, "NaSearCh", "DescriPtion", true, user2, null, null);
        itemRepository.save(item);
        item = new Item(3, "Search", "Dessss", true, user2, null, null);
        itemRepository.save(item);
    }

    @Test
    void findAllByOwnerIdOrderById_whenRightRequest_thenResultTwoSize() {
        List<Item> listActual = itemRepository.findAllByOwnerIdOrderById(userId2, null).getContent();

        assertEquals(2, listActual.size());
        assertEquals(userId2, listActual.get(0).getOwner().getId());
    }

    @Test
    void findAllByOwnerIdOrderById_whenBadIdUser_thenEmptyList() {
        List<Item> listActual = itemRepository.findAllByOwnerIdOrderById(99, null).getContent();

        assertEquals(0, listActual.size());
    }

    @Test
    void findItemsByTextSearch_whenSearchText_thenResultThreeSize() {
        String search = "search";
        List<Item> listActual = itemRepository.findItemsByTextSearch(search, search, null).getContent();

        assertEquals(3, listActual.size());
    }

    @Test
    void findItemsByTextSearch_whenSearchText_thenResultTwoSize() {
        String search = "description";
        List<Item> listActual = itemRepository.findItemsByTextSearch(search, search, null).getContent();

        assertEquals(2, listActual.size());
    }

    @Test
    void findItemsByTextSearch_whenSearchText_thenResultEmpty() {
        String search = "findItemsByText";
        List<Item> listActual = itemRepository.findItemsByTextSearch(search, search, null).getContent();

        assertEquals(0, listActual.size());
    }

}