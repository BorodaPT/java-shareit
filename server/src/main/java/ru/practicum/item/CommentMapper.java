package ru.practicum.item;

import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDTO toDTO (Comment comment) {
        if (comment == null) return null;
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDTO> toDTO (Iterable<Comment> comments) {
        List<CommentDTO> result = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                result.add(toDTO(comment));
            }
        }
        return result;
    }

    public static Comment toComment(CommentDTO commentDTO, Item item, User user, LocalDateTime created) {
        return new Comment(
                commentDTO.getId(),
                commentDTO.getText(),
                item,
                user,
                created
        );
    }



}
