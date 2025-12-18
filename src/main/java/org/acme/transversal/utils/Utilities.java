package org.acme.transversal.utils;

import jakarta.ws.rs.core.HttpHeaders;

import java.util.Optional;

public class Utilities {

    public static Optional<String> getSpecificHeader(HttpHeaders httpHeaders, String headerName) {

        String header = httpHeaders.getHeaderString(headerName);
        return Optional.ofNullable(header);
    }
}
