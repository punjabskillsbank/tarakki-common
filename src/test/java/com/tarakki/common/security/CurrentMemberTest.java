package com.tarakki.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrentMemberTest {

    private UUID memberId;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        memberId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void find_shouldReturnMemberIdWhenAuthenticationNameIsAMemberId() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(memberId.toString(), null, "ROLE_USER"));

        Optional<UUID> result = CurrentMember.find();

        assertEquals(Optional.of(memberId), result);
    }

    @Test
    void find_shouldReturnEmptyWhenNobodyIsAuthenticated() {
        assertTrue(CurrentMember.find().isEmpty());
    }

    @Test
    void find_shouldReturnEmptyWhenAuthenticationNameIsNotAUuid() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", null, "ROLE_USER"));

        assertTrue(CurrentMember.find().isEmpty());
    }

    @Test
    void find_shouldReturnEmptyForAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertTrue(CurrentMember.find().isEmpty());
    }

    @Test
    void require_shouldReturnMemberIdWhenAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(memberId.toString(), null, "ROLE_USER"));

        assertEquals(memberId, CurrentMember.require());
    }

    @Test
    void require_shouldThrowWhenNobodyIsAuthenticated() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, CurrentMember::require);
    }
}
