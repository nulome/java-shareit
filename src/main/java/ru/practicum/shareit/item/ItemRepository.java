package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> getItemById(int id);

    Page<Item> findAllByOwnerIdOrderById(int id, Pageable pageable);

    @Query("select i " +
            "from Item as i " +
            "where UPPER(i.name) like UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) like UPPER(CONCAT('%', ?1, '%')) " +
            "AND i.available = TRUE ")
    Page<Item> findItemsByTextSearch(String text, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.item.dto.ItemShortDto(it.id, it.name, it.description, it.available," +
            " it.owner.id, it.request.id) FROM Item AS it " +
            "WHERE it.request.id IN ?1 ")
    List<ItemShortDto> findAllByRequestIdInList(List<Integer> listIds);
}
