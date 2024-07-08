package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> getUserById(int id);

    @Modifying
    @Query("update User u set u.name = :name where u.id = :id")
    void updateName(@Param(value = "id") int id, @Param(value = "name") String name);

    @Modifying
    @Query("update User u set u.email = :email where u.id = :id")
    void updateUserEmail(@Param(value = "id") int id, @Param(value = "email") String email);


}
