package com.tarakki.common.exceptionHandling;

public class OrganisationNotFoundException extends RuntimeException {

    public OrganisationNotFoundException(Long orgId) {
        super("Organisation with id " + orgId + " not found");
    }
}
