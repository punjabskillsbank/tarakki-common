package com.tarakki.common.exceptionHandling;

import java.util.UUID;

    public class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(UUID memberId) {
            super("Member not found at id: " + memberId);
        }
    }
