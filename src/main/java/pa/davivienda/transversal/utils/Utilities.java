package pa.davivienda.transversal.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * Clase de utilidades transversales para el microservicio PER002.
 * 
 * <p>Proporciona métodos auxiliares para:</p>
 * <ul>
 *   <li>Extracción segura de headers HTTP</li>
 *   <li>Formateo de fechas con timezone UTC-6 (América Central)</li>
 * </ul>
 * 
 * <p>Todas las operaciones son stateless y thread-safe.</p>
 * 
 * @author Equipo PER002
 * @version 1.0.0
 * @since 2025-12-24
 */
public class Utilities {

    public static Optional<String> getSpecificHeader(HttpHeaders httpHeaders, String headerName) {

        String header = httpHeaders.getHeaderString(headerName);
        return Optional.ofNullable(header);
    }

    /**
     * Obtiene la fecha y hora actual formateada según especificación del BUS.
     * 
     * <p>Genera un timestamp en formato ISO 8601 con timezone UTC-6 (América Central):</p>
     * <pre>yyyy-MM-dd'T'HH:mm:ssXXX</pre>
     * 
     * <p>Ejemplo de salida: {@code 2025-12-24T10:30:45-06:00}</p>
     * 
     * @return String con la fecha formateada
     */
    public static String getDateNowFormatted() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("-06:00"));

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        return now.format(formatter);
    }
}
