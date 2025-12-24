package pa.davivienda.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;
import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.entities.Customer;
import pa.davivienda.domain.entities.TransactionCost;
import pa.davivienda.domain.ports.output.AuditPort;
import pa.davivienda.persistence.repositories.Per002StatelessRepository;

/**
 * Tests unitarios para Per002UseCaseImpl
 * Objetivo: 85% cobertura
 */
@ExtendWith(MockitoExtension.class)
class Per002UseCaseImplTest {

    @Mock
    private Per002StatelessRepository statelessRepository;

    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private Per002UseCaseImpl per002UseCase;

    private HeadersPer002RequestDto validHeaders;
    private Per002RequestDto validRequest;
    private Customer mockCustomer;
    private TransactionCost mockTransactionCost;

    @BeforeEach
    void setUp() {
        // Headers válidos
        validHeaders = HeadersPer002RequestDto.builder()
                .nombreOperacion("consultaCosto")
                .total(1)
                .jornada((short) 1)
                .canal((short) 81)
                .modoOperacion((short) 1)
                .usuario("testuser")
                .perfil((short) 1)
                .versionServicio("1.0")
                .idTransaccion("TXN123456")
                .build();

        // Request válido
        validRequest = Per002RequestDto.builder()
                .codTipoIdentificacion("CED")
                .valNumeroIdentificacion("8-111-111")
                .codTipoConcepto("COBPER")
                .codPais("PA")
                .build();

        // Customer mock
        mockCustomer = new Customer();
        mockCustomer.setCustomerId("12345");
        mockCustomer.setIdentificationType("CED");
        mockCustomer.setIdentificationNumber("8-111-111");

        // TransactionCost mock
        mockTransactionCost = new TransactionCost();
        mockTransactionCost.setTransactionCode("01PAR157");
        mockTransactionCost.setCost(500); // Costo en centavos (5.00 USD)
        mockTransactionCost.setCurrencyCode("USD");
        mockTransactionCost.setCustomerId("12345");
    }

    // ============== TESTS DE ÉXITO ==============

    @Test
    void whenValidRequestWithCanal81AndCOBPER_thenReturnResponse() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getCostoDeLaTransaccion()); // 500 centavos = 5.00 USD
        assertEquals("USD", response.getCodMonedaTransaccion());
        assertNotNull(response.getFecHoraMovimiento());
        
        verify(statelessRepository).findCustomerByDocument("CED", "8-111-111");
        verify(statelessRepository).findTransactionCost("12345", "01PAR157");
        verify(auditPort, times(6)).logAsync(any()); // 6 puntos de auditoría: ENTRADA, TRAMA_OUT(2), TRAMA_IN(2), SALIDA
    }

    @Test
    void whenValidRequestWithCanal151AndTRCPRO_thenReturnResponse() {
        // Arrange
        validHeaders.setCanal((short) 151);
        validRequest.setCodTipoConcepto("TRCPRO");
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR153"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertNotNull(response);
        verify(statelessRepository).findTransactionCost("12345", "01PAR153");
    }

    @Test
    void whenValidRequestWithTRCTER_thenHomologateCorrectly() {
        // Arrange
        validHeaders.setCanal((short) 151);
        validRequest.setCodTipoConcepto("TRCTER");
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR154"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertNotNull(response);
        verify(statelessRepository).findTransactionCost("12345", "01PAR154");
    }

    @Test
    void whenValidRequestWithAllowedCountryCR_thenReturnResponse() {
        // Arrange
        validRequest.setCodPais("CR");
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertNotNull(response);
    }

    // ============== TESTS DE VALIDACIÓN DE CAMPOS OBLIGATORIOS ==============

    @Test
    void whenCodTipoIdentificacionNull_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoIdentificacion(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codTipoIdentificacion es obligatorio", exception.getMessage());
        verify(auditPort, times(2)).logAsync(any()); // ENTRADA + ERROR
    }

    @Test
    void whenCodTipoIdentificacionEmpty_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoIdentificacion("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codTipoIdentificacion es obligatorio", exception.getMessage());
    }

    @Test
    void whenValNumeroIdentificacionNull_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setValNumeroIdentificacion(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("valNumeroIdentificacion es obligatorio", exception.getMessage());
    }

    @Test
    void whenValNumeroIdentificacionEmpty_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setValNumeroIdentificacion("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("valNumeroIdentificacion es obligatorio", exception.getMessage());
    }

    @Test
    void whenCodTipoConceptoNull_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoConcepto(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codTipoConcepto es obligatorio", exception.getMessage());
    }

    @Test
    void whenCodTipoConceptoEmpty_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoConcepto(" ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codTipoConcepto es obligatorio", exception.getMessage());
    }

    @Test
    void whenCodPaisNull_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodPais(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codPais es obligatorio", exception.getMessage());
    }

    @Test
    void whenCodPaisEmpty_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodPais("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("codPais es obligatorio", exception.getMessage());
    }

    // ============== TESTS DE VALIDACIÓN DE CANAL ==============

    @Test
    void whenCanalInvalido_thenThrowIllegalArgument() {
        // Arrange
        validHeaders.setCanal((short) 99);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Canal inválido. Solo se permiten 81 o 151", exception.getMessage());
    }

    @Test
    void whenCanal200_thenThrowIllegalArgument() {
        // Arrange
        validHeaders.setCanal((short) 200);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Canal inválido. Solo se permiten 81 o 151", exception.getMessage());
    }

    // ============== TESTS DE VALIDACIÓN DE PAÍS ==============

    @Test
    void whenCodPaisInvalido_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodPais("XX");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Código de país no permitido", exception.getMessage());
    }

    @Test
    void whenCodPaisMX_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodPais("MX");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Código de país no permitido", exception.getMessage());
    }

    // ============== TESTS DE VALIDACIÓN DE CONCEPTO ==============

    @Test
    void whenConceptoInvalido_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoConcepto("INVALID");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Código de concepto no pertenece al catálogo PER", exception.getMessage());
    }

    @Test
    void whenConceptoXYZ_thenThrowIllegalArgument() {
        // Arrange
        validRequest.setCodTipoConcepto("XYZ123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Código de concepto no pertenece al catálogo PER", exception.getMessage());
    }

    // ============== TESTS DE VALIDACIÓN CANAL-CONCEPTO ==============

    @Test
    void whenCanal81AndConceptoNotCOBPER_thenThrowIllegalArgument() {
        // Arrange
        validHeaders.setCanal((short) 81);
        validRequest.setCodTipoConcepto("TRCPRO");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Canal 81 solo permite concepto COBPER", exception.getMessage());
    }

    @Test
    void whenCanal151AndConceptoCOBPER_thenThrowIllegalArgument() {
        // Arrange
        validHeaders.setCanal((short) 151);
        validRequest.setCodTipoConcepto("COBPER");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Canal 151 no permite concepto COBPER", exception.getMessage());
    }

    // ============== TESTS DE HOMOLOGACIÓN ==============

    @Test
    void whenConceptoCOBPER_thenHomologateTo01PAR157() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        verify(statelessRepository).findTransactionCost("12345", "01PAR157");
    }

    @Test
    void whenConceptoTRCPRO_thenHomologateTo01PAR153() {
        // Arrange
        validHeaders.setCanal((short) 151);
        validRequest.setCodTipoConcepto("TRCPRO");
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR153"))
                .thenReturn(mockTransactionCost);

        // Act
        per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        verify(statelessRepository).findTransactionCost("12345", "01PAR153");
    }

    @Test
    void whenConceptoTRCTER_thenHomologateTo01PAR154() {
        // Arrange
        validHeaders.setCanal((short) 151);
        validRequest.setCodTipoConcepto("TRCTER");
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR154"))
                .thenReturn(mockTransactionCost);

        // Act
        per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        verify(statelessRepository).findTransactionCost("12345", "01PAR154");
    }

    // ============== TESTS DE CONSULTA A REPOSITORIO ==============

    @Test
    void whenCustomerNotFound_thenThrowIllegalArgument() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("El usuario no existe", exception.getMessage());
        
        verify(statelessRepository).findCustomerByDocument("CED", "8-111-111");
        verify(statelessRepository, never()).findTransactionCost(anyString(), anyString());
        verify(auditPort, times(3)).logAsync(any()); // ENTRADA, TRAMA_OUT, ERROR
    }

    @Test
    void whenTransactionCostNotFound_thenThrowIllegalArgument() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );
        assertEquals("Data no encontrada", exception.getMessage());
        
        verify(statelessRepository).findCustomerByDocument("CED", "8-111-111");
        verify(statelessRepository).findTransactionCost("12345", "01PAR157");
        verify(auditPort, times(5)).logAsync(any()); // ENTRADA, TRAMA_OUT(2), TRAMA_IN, ERROR
    }

    // ============== TESTS DE AUDITORÍA ==============

    @Test
    void whenSuccess_thenAudit6Points() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        // Verificar 6 puntos: ENTRADA + TRAMA_OUT + TRAMA_IN + TRAMA_OUT + TRAMA_IN + SALIDA
        verify(auditPort, times(6)).logAsync(any());
    }

    @Test
    void whenError_thenAuditEntradaAndError() {
        // Arrange
        validRequest.setCodTipoIdentificacion(null);

        // Act
        assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );

        // Assert
        // Verificar 2 puntos: ENTRADA + ERROR
        verify(auditPort, times(2)).logAsync(any());
    }

    @Test
    void whenCustomerNotFound_thenAudit3Points() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(null);

        // Act
        assertThrows(IllegalArgumentException.class, () ->
                per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest)
        );

        // Assert
        // Verificar 3 puntos: ENTRADA + TRAMA_OUT + ERROR
        verify(auditPort, times(3)).logAsync(any());
    }

    // ============== TESTS DE RESPUESTA ==============

    @Test
    void whenSuccess_thenResponseHasAllFields() {
        // Arrange
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getFecHoraMovimiento());
        assertNotNull(response.getCodMonedaTransaccion());
        assertTrue(response.getCostoDeLaTransaccion() >= 0);
        
        assertEquals("USD", response.getCodMonedaTransaccion());
        assertEquals(500, response.getCostoDeLaTransaccion()); // 500 centavos = 5.00 USD
    }

    @Test
    void whenSuccessWithDifferentCurrency_thenResponseReflectsCurrency() {
        // Arrange
        mockTransactionCost.setCurrencyCode("EUR");
        mockTransactionCost.setCost(1050); // 1050 centavos = 10.50 EUR
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertEquals("EUR", response.getCodMonedaTransaccion());
        assertEquals(1050, response.getCostoDeLaTransaccion());
    }

    @Test
    void whenSuccessWithZeroCost_thenResponseHasZero() {
        // Arrange
        mockTransactionCost.setCost(0);
        
        when(statelessRepository.findCustomerByDocument("CED", "8-111-111"))
                .thenReturn(mockCustomer);
        when(statelessRepository.findTransactionCost("12345", "01PAR157"))
                .thenReturn(mockTransactionCost);

        // Act
        Per002ResponseDto response = per002UseCase.consultaCostoTransaccionPer(validHeaders, validRequest);

        // Assert
        assertEquals(0, response.getCostoDeLaTransaccion());
    }
}
