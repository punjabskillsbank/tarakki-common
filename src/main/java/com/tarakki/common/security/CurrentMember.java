package com.tarakki.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Gives services the id of the member making the current request, taken from the authenticated user.
 * <p>
 * Every Tarakki service names its authentication after the member id: the JWT {@code sub} for the
 * resource-server services, and the member id principal for member-service's own filter. That is why
 * {@link Authentication#getName()} is all that is needed here.
 */
public final class CurrentMember {

    private CurrentMember() {
    }

    public static Optional<UUID> find() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(authentication.getName()));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    public static UUID require() {
        return find().orElseThrow(() ->
                new AuthenticationCredentialsNotFoundException("No authenticated member for this request"));
    }
}
