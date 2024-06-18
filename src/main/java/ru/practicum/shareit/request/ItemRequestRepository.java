package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Optional<ItemRequest> getItemRequestById(int id);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(int id);

    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(int userId, Pageable pageable);
}
