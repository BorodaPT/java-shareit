package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository  extends JpaRepository<ItemRequest, Long> {

    @Query(value = "select req.* " +
            "from requests as req "+
            "where req.requestor_id = ?1 " +
            "order by created desc", nativeQuery = true)
    List<ItemRequest> findByRequestorId(long userId);


    @Query(value = "select req.* " +
            "from requests as req "+
            "where req.requestor_id <> ?1 " +
            "order by created desc", nativeQuery = true)
    Page<ItemRequest> findAll(long userId, Pageable pageable);

    @Query(value = "select req.* " +
            "from requests as req "+
            "where req.requestor_id <> ?1 " +
            "order by created desc", nativeQuery = true)
    List<ItemRequest> findAllRequest(long userId);

}
