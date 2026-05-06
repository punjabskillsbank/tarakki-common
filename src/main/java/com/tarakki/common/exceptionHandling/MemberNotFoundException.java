package com.tarakki.common.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(UUID memberId) {
            super("Member not found at id: " + memberId);
        }
    }
