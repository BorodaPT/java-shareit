package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwner_id(Long userId, Pageable pageable);

    List<Item> findByOwner_id(Long userId);

    @Query(value = "select * " +
            "from items as it "+
            "where it.id = ?1 AND is_available = ?2 ", nativeQuery = true)
    Optional<Item> findByIdAndIs_available(Long id, Boolean aBoolean);


    @Query(value = "select * " +
            "from items as it "+
            "where upper(it.Name) like upper(concat('%', ?1, '%')) OR upper(it.Description) like upper(concat('%', ?1, '%')) and is_available = true "+
            "order by it.id ", nativeQuery = true)
    Page<Item> findByNameContainingOrDescriptionContaining(String substring, Pageable pageable);

    @Query(value = "select * " +
            "from items as it "+
            "where upper(it.Name) like upper(concat('%', ?1, '%')) OR upper(it.Description) like upper(concat('%', ?1, '%')) and is_available = true "+
            "order by it.id ", nativeQuery = true)
    List<Item> findByNameContainingOrDescriptionContaining(String substring);

    Long countByOwner_id(Long userId);

    @Query(value = "select * " +
            "from items as it "+
            "where it.request_id = ?1 ", nativeQuery = true)
    List<Item> findByRequest_id(long idRequest);
}
