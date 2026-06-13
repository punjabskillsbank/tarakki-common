package com.tarakki.common.exceptionHandling;

import java.util.UUID;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(UUID memberId) {
            super("Member not found at id: " + memberId);
        }
    }
