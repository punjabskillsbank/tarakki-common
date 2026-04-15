package com.tarakki.common.exceptionHandling;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(UUID userId) {
        super("User not found at id:" +userId);
    }
}
