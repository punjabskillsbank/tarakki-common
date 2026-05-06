package com.tarakki.common.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

    public class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(UUID memberId) {
            super("Member not found at id: " + memberId);
        }
    }
