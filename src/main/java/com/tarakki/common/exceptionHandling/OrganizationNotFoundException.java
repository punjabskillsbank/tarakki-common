package com.tarakki.common.exceptionHandling;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(Long orgId) {
        super("Organization with id " + orgId + " not found");
    }
}
