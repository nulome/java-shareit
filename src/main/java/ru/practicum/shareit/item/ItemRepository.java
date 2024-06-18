package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> getItemById(int id);

    Page<Item> findAllByOwnerIdOrderById(int id, Pageable pageable);

    @Query("select i " +
            "from Item as i " +
            "where UPPER(i.name) like UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) like UPPER(CONCAT('%', ?2, '%')) " +
            "AND i.available = TRUE ")
    Page<Item> findItemsByTextSearch(String text, String text2, Pageable pageable);


}
