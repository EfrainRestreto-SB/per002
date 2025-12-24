package pa.davivienda.webapi.validators;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;
import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para InputHeadersPer002Validator
 * Objetivo: 100% cobertura
 */
class InputHeadersPer002ValidatorTest {

    private HttpHeaders createMockHeaders(String nombreOperacion, String total, String jornada,
                                          String canal, String modoOperacion, String usuario,
                                          String perfil, String versionServicio, String idTransaccion) {
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getHeaderString("nombreOperacion")).thenReturn(nombreOperacion);
        when(httpHeaders.getHeaderString("Total")).thenReturn(total);
        when(httpHeaders.getHeaderString("jornada")).thenReturn(jornada);
        when(httpHeaders.getHeaderString("Canal")).thenReturn(canal);
        when(httpHeaders.getHeaderString("modoDeOperacion")).thenReturn(modoOperacion);
        when(httpHeaders.getHeaderString("usuario")).thenReturn(usuario);
        when(httpHeaders.getHeaderString("perfil")).thenReturn(perfil);
        when(httpHeaders.getHeaderString("versionServicio")).thenReturn(versionServicio);
        when(httpHeaders.getHeaderString("idTransaccion")).thenReturn(idTransaccion);
        return httpHeaders;
    }

    private HttpHeaders createValidHeaders() {
        return createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                "testuser", "1", "1.0", "TXN123456");
    }

    @Test
    void whenAllHeadersValid_thenReturnDto() {
        // Arrange
        HttpHeaders httpHeaders = createValidHeaders();

        // Act
        HeadersPer002RequestDto result = InputHeadersPer002Validator.validateInputHeaders(httpHeaders);

        // Assert
        assertNotNull(result);
        assertEquals("consultaCosto", result.getNombreOperacion());
        assertEquals(1, result.getTotal());
        assertEquals((short) 1, result.getJornada());
        assertEquals((short) 81, result.getCanal());
        assertEquals((short) 1, result.getModoOperacion());
        assertEquals("testuser", result.getUsuario());
        assertEquals((short) 1, result.getPerfil());
        assertEquals("1.0", result.getVersionServicio());
        assertEquals("TXN123456", result.getIdTransaccion());
    }

    @Test
    void whenNombreOperacionMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders(null, "1", "1", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: nombreOperacion", exception.getMessage());
    }

    @Test
    void whenTotalMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", null, "1", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: Total", exception.getMessage());
    }

    @Test
    void whenTotalEmpty_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "   ", "1", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'Total' no puede estar vacío", exception.getMessage());
    }

    @Test
    void whenTotalInvalidFormat_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "abc", "1", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'Total' debe ser un número entero válido", exception.getMessage());
    }

    @Test
    void whenJornadaMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", null, "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: jornada", exception.getMessage());
    }

    @Test
    void whenJornadaEmpty_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'jornada' no puede estar vacío", exception.getMessage());
    }

    @Test
    void whenJornadaInvalidFormat_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "xyz", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'jornada' debe ser un número válido", exception.getMessage());
    }

    @Test
    void whenCanalMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", null, "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: Canal", exception.getMessage());
    }

    @Test
    void whenCanalEmpty_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "  ", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'Canal' no puede estar vacío", exception.getMessage());
    }

    @Test
    void whenCanalInvalidFormat_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "invalid", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'Canal' debe ser un número válido", exception.getMessage());
    }

    @Test
    void whenModoOperacionMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", null, 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: modoDeOperacion", exception.getMessage());
    }

    @Test
    void whenModoOperacionEmpty_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'modoDeOperacion' no puede estar vacío", exception.getMessage());
    }

    @Test
    void whenModoOperacionInvalidFormat_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "abc", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'modoDeOperacion' debe ser un número válido", exception.getMessage());
    }

    @Test
    void whenUsuarioMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   null, "1", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: usuario", exception.getMessage());
    }

    @Test
    void whenPerfilMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   "testuser", null, "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: perfil", exception.getMessage());
    }

    @Test
    void whenPerfilEmpty_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   "testuser", " ", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'perfil' no puede estar vacío", exception.getMessage());
    }

    @Test
    void whenPerfilInvalidFormat_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   "testuser", "notanumber", "1.0", "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Header 'perfil' debe ser un número válido", exception.getMessage());
    }

    @Test
    void whenVersionServicioMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   "testuser", "1", null, "TXN123456");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: versionServicio", exception.getMessage());
    }

    @Test
    void whenIdTransaccionMissing_thenThrowBadRequest() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "81", "1", 
                                                   "testuser", "1", "1.0", null);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> 
            InputHeadersPer002Validator.validateInputHeaders(httpHeaders)
        );
        assertEquals("Missing header: idTransaccion", exception.getMessage());
    }

    @Test
    void whenCanal151_thenParsedCorrectly() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "1", "1", "151", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act
        HeadersPer002RequestDto result = InputHeadersPer002Validator.validateInputHeaders(httpHeaders);

        // Assert
        assertEquals((short) 151, result.getCanal());
    }

    @Test
    void whenLargeTotal_thenParsedCorrectly() {
        // Arrange
        HttpHeaders httpHeaders = createMockHeaders("consultaCosto", "999999", "1", "81", "1", 
                                                   "testuser", "1", "1.0", "TXN123456");

        // Act
        HeadersPer002RequestDto result = InputHeadersPer002Validator.validateInputHeaders(httpHeaders);

        // Assert
        assertEquals(999999, result.getTotal());
    }
}
