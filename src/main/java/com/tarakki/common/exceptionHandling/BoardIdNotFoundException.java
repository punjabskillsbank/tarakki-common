package com.tarakki.common.exceptionHandling;

public class BoardIdNotFoundException extends RuntimeException {

    public BoardIdNotFoundException(Long boardId) {
        super("Board not found with id: " + boardId);
    }
}
