package com.tarakki.common.exceptionHandling;

public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException(Long boardId) {
        super("Board not found with id: " + boardId);
    }
}
