package pa.davivienda.transversal.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.HttpHeaders;

/**
 * Tests unitarios para Utilities.
 * 
 * Verifica funcionalidad de:
 * - Extracción segura de headers HTTP
 * - Formateo de fechas con timezone UTC-6
 */
@QuarkusTest
class UtilitiesTest {

    // ============== CONSTANTES DE TEST ==============
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_X_CUSTOM = "X-Custom-Header";
    private static final String HEADER_NOMBRE_OPERACION = "nombreOperacion";
    private static final String HEADER_CANAL = "Canal";
    private static final String HEADER_USUARIO = "usuario";
    private static final String HEADER_TOTAL = "Total";
    private static final String HEADER_JORNADA = "jornada";
    private static final String HEADER_ID_TRANSACCION = "idTransaccion";
    private static final String HEADER_X_MISSING = "X-Missing";
    
    private static final String VALUE_CONTENT_TYPE_JSON = "application/json";
    private static final String VALUE_USER_AGENT = "  Mozilla/5.0  ";
    private static final String VALUE_OPERACION_CONSULTA = "consultaCosto";
    private static final String VALUE_CANAL_81 = "81";
    private static final String VALUE_USUARIO_ADMIN = "admin";
    private static final String VALUE_TOTAL_1 = "1";
    private static final String VALUE_JORNADA_1 = "1";
    private static final String VALUE_ID_TRANSACCION = "TXN123";
    
    private static final String TIMEZONE_UTC_MINUS_6 = "-06:00";
    private static final String DATE_REGEX_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}-06:00";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String DATE_PART_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    private static final String TIME_PART_REGEX = "\\d{2}:\\d{2}:\\d{2}-06:00";

    // ============== TESTS DE getSpecificHeader ==============

    @Test
    void whenHeaderExists_thenReturnsOptionalWithValue() {
        // Given
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_CONTENT_TYPE)).thenReturn(VALUE_CONTENT_TYPE_JSON);

        // When
        Optional<String> result = Utilities.getSpecificHeader(headers, HEADER_CONTENT_TYPE);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(VALUE_CONTENT_TYPE_JSON);
    }

    @Test
    void whenHeaderDoesNotExist_thenReturnsEmptyOptional() {
        // Given
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_X_CUSTOM)).thenReturn(null);

        // When
        Optional<String> result = Utilities.getSpecificHeader(headers, HEADER_X_CUSTOM);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void whenHeaderIsEmpty_thenReturnsOptionalWithEmptyString() {
        // Given
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_AUTHORIZATION)).thenReturn("");

        // When
        Optional<String> result = Utilities.getSpecificHeader(headers, HEADER_AUTHORIZATION);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void whenHeaderHasWhitespace_thenReturnsOptionalWithWhitespace() {
        // Given
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_USER_AGENT)).thenReturn(VALUE_USER_AGENT);

        // When
        Optional<String> result = Utilities.getSpecificHeader(headers, HEADER_USER_AGENT);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(VALUE_USER_AGENT);
    }

    @Test
    void whenMultipleHeadersCalled_thenReturnsCorrectValues() {
        // Given
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_NOMBRE_OPERACION)).thenReturn(VALUE_OPERACION_CONSULTA);
        when(headers.getHeaderString(HEADER_CANAL)).thenReturn(VALUE_CANAL_81);
        when(headers.getHeaderString(HEADER_USUARIO)).thenReturn(VALUE_USUARIO_ADMIN);

        // When
        Optional<String> op = Utilities.getSpecificHeader(headers, HEADER_NOMBRE_OPERACION);
        Optional<String> canal = Utilities.getSpecificHeader(headers, HEADER_CANAL);
        Optional<String> user = Utilities.getSpecificHeader(headers, HEADER_USUARIO);

        // Then
        assertThat(op).hasValue(VALUE_OPERACION_CONSULTA);
        assertThat(canal).hasValue(VALUE_CANAL_81);
        assertThat(user).hasValue(VALUE_USUARIO_ADMIN);
    }

    // ============== TESTS DE getDateNowFormatted ==============

    @Test
    void whenGetDateNowFormatted_thenReturnsISO8601Format() {
        // When
        String result = Utilities.getDateNowFormatted();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Formato: 2025-12-24T10:30:45-06:00
        assertThat(result).matches(DATE_REGEX_PATTERN);
    }

    @Test
    void whenGetDateNowFormatted_thenTimezoneIsUTCMinus6() {
        // When
        String result = Utilities.getDateNowFormatted();

        // Then
        assertThat(result).endsWith(TIMEZONE_UTC_MINUS_6);
    }

    @Test
    void whenGetDateNowFormattedCalledMultipleTimes_thenTimestampsAreRecent() {
        // When
        String timestamp1 = Utilities.getDateNowFormatted();
        String timestamp2 = Utilities.getDateNowFormatted();

        // Then
        assertThat(timestamp1).isNotNull();
        assertThat(timestamp2).isNotNull();
        
        // Parse y verificar que son recientes (dentro del mismo minuto)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        OffsetDateTime dt1 = OffsetDateTime.parse(timestamp1, formatter);
        OffsetDateTime dt2 = OffsetDateTime.parse(timestamp2, formatter);
        
        assertThat(dt1.getOffset()).isEqualTo(ZoneOffset.of(TIMEZONE_UTC_MINUS_6));
        assertThat(dt2.getOffset()).isEqualTo(ZoneOffset.of(TIMEZONE_UTC_MINUS_6));
        
        // Verificar que están en el mismo minuto (ejecución rápida)
        assertThat(dt1.getYear()).isEqualTo(dt2.getYear());
        assertThat(dt1.getMonth()).isEqualTo(dt2.getMonth());
        assertThat(dt1.getDayOfMonth()).isEqualTo(dt2.getDayOfMonth());
        assertThat(dt1.getHour()).isEqualTo(dt2.getHour());
        assertThat(dt1.getMinute()).isEqualTo(dt2.getMinute());
    }

    @Test
    void whenGetDateNowFormatted_thenDateHasCorrectComponents() {
        // When
        String result = Utilities.getDateNowFormatted();

        // Then
        String[] parts = result.split("T");
        assertThat(parts).hasSize(2);
        
        // Verificar parte de fecha: yyyy-MM-dd
        String datePart = parts[0];
        assertThat(datePart).matches(DATE_PART_REGEX);
        
        // Verificar parte de tiempo: HH:mm:ss-06:00
        String timePart = parts[1];
        assertThat(timePart).matches(TIME_PART_REGEX);
    }

    @Test
    void whenGetDateNowFormatted_thenCanBeParsedBack() {
        // When
        String formatted = Utilities.getDateNowFormatted();

        // Then - Verificar que se puede parsear de vuelta
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        OffsetDateTime parsed = OffsetDateTime.parse(formatted, formatter);
        
        assertThat(parsed).isNotNull();
        assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.of(TIMEZONE_UTC_MINUS_6));
    }

    // ============== TESTS DE INTEGRACIÓN ==============

    @Test
    void whenHeaderExtractionUsedInRealScenario_thenWorksCorrectly() {
        // Given - Simular headers de un request real
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        when(headers.getHeaderString(HEADER_NOMBRE_OPERACION)).thenReturn(VALUE_OPERACION_CONSULTA);
        when(headers.getHeaderString(HEADER_TOTAL)).thenReturn(VALUE_TOTAL_1);
        when(headers.getHeaderString(HEADER_JORNADA)).thenReturn(VALUE_JORNADA_1);
        when(headers.getHeaderString(HEADER_CANAL)).thenReturn(VALUE_CANAL_81);
        when(headers.getHeaderString(HEADER_ID_TRANSACCION)).thenReturn(VALUE_ID_TRANSACCION);

        // When - Extraer headers como en el código real
        Optional<String> nombreOperacion = Utilities.getSpecificHeader(headers, HEADER_NOMBRE_OPERACION);
        Optional<String> total = Utilities.getSpecificHeader(headers, HEADER_TOTAL);
        Optional<String> jornada = Utilities.getSpecificHeader(headers, HEADER_JORNADA);
        Optional<String> canal = Utilities.getSpecificHeader(headers, HEADER_CANAL);
        Optional<String> idTransaccion = Utilities.getSpecificHeader(headers, HEADER_ID_TRANSACCION);
        Optional<String> missing = Utilities.getSpecificHeader(headers, HEADER_X_MISSING);

        // Then
        assertThat(nombreOperacion).hasValue(VALUE_OPERACION_CONSULTA);
        assertThat(total).hasValue(VALUE_TOTAL_1);
        assertThat(jornada).hasValue(VALUE_JORNADA_1);
        assertThat(canal).hasValue(VALUE_CANAL_81);
        assertThat(idTransaccion).hasValue(VALUE_ID_TRANSACCION);
        assertThat(missing).isEmpty();
    }

    @Test
    void whenTimestampUsedForAudit_thenFormatIsCorrect() {
        // When - Generar timestamp como en auditoría
        String auditTimestamp = Utilities.getDateNowFormatted();

        // Then - Verificar formato para auditoría
        assertThat(auditTimestamp).isNotNull();
        assertThat(auditTimestamp).contains("T"); // Separador ISO 8601
        assertThat(auditTimestamp).endsWith(TIMEZONE_UTC_MINUS_6); // Timezone América Central
        
        // Verificar que puede ser usado en logs/auditoría
        String logMessage = "Audit - Timestamp: " + auditTimestamp;
        assertThat(logMessage).contains(auditTimestamp);
    }
}
