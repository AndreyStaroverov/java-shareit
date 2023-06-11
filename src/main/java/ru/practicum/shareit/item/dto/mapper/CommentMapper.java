package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getDateCreated()
        );
    }

    public static List<CommentDto> toCommentDtoCollection(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    comment.getUser().getName(),
                    comment.getDateCreated()
            ));
        }
        return commentsDto;
    }
}
