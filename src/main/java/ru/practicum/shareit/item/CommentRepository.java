package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select * " +
            "from comments as com "+
            "where item_id = ?1 ", nativeQuery = true)
    List<Comment> findAllByIdItem(Long id);
}