package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;


import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_id(Long userId);

    @Query(value = "select * " +
            "from items as it "+
            "where it.id = ?1 AND is_available = ?2 ", nativeQuery = true)
    Optional<Item> findByIdAndIs_available(Long id, Boolean aBoolean);

    List<Item> findAll();

    @Query(value = "select * " +
            "from items as it "+
            "where upper(it.Name) like upper(concat('%', ?1, '%')) OR upper(it.Description) like upper(concat('%', ?1, '%')) and is_available = true "+
            "order by it.id ", nativeQuery = true)
    List<Item> findByNameContainingOrDescriptionContaining(String substring);

    Long countByOwner_id(Long userId);
}
