package pa.davivienda.transversal.utils;

import jakarta.ws.rs.core.HttpHeaders;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Utilities {

    public static Optional<String> getSpecificHeader(HttpHeaders httpHeaders, String headerName) {

        String header = httpHeaders.getHeaderString(headerName);
        return Optional.ofNullable(header);
    }

    public static String getDateNowFormatted() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("-06:00"));

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        return now.format(formatter);
    }
}
