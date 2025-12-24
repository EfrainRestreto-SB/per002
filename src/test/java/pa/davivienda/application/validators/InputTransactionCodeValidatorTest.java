package pa.davivienda.application.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para InputTransactionCodeValidator
 * Objetivo: 100% cobertura
 */
class InputTransactionCodeValidatorTest {

    // Tests para getTransactionCodeHomologate
    @Test
    void whenTransactionCodeIsCOBPER_thenReturn01PAR157() {
        // Act
        String result = InputTransactionCodeValidator.getTransactionCodeHomologate("COBPER");

        // Assert
        assertEquals("01PAR157", result);
    }

    @Test
    void whenTransactionCodeIsTRCPRO_thenReturn01PAR153() {
        // Act
        String result = InputTransactionCodeValidator.getTransactionCodeHomologate("TRCPRO");

        // Assert
        assertEquals("01PAR153", result);
    }

    @Test
    void whenTransactionCodeIsTRCTER_thenReturn01PAR154() {
        // Act
        String result = InputTransactionCodeValidator.getTransactionCodeHomologate("TRCTER");

        // Assert
        assertEquals("01PAR154", result);
    }

    @Test
    void whenTransactionCodeIsInvalid_thenThrowIllegalArgument() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            InputTransactionCodeValidator.getTransactionCodeHomologate("INVALID")
        );
        assertEquals("Codigo de transaccion no permitido", exception.getMessage());
    }

    // Tests para codPaisValidate
    @Test
    void whenCodPaisIsPA_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("PA");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsCR_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("CR");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsCO_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("CO");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsSV_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("SV");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsHN_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("HN");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsUS_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("US");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenCodPaisIsInvalid_thenReturnFalse() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("XX");

        // Assert
        assertFalse(result);
    }

    @Test
    void whenCodPaisIsEmpty_thenReturnFalse() {
        // Act
        boolean result = InputTransactionCodeValidator.codPaisValidate("");

        // Assert
        assertFalse(result);
    }

    // Tests para conceptoPerValidate
    @Test
    void whenConceptoIsCOBPER_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("COBPER");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTRCPRO_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TRCPRO");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTRCTER_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TRCTER");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTRA11R_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TRA11R");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTR1VR_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TR1VR");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTININD_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TININD");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsTINARC_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("TINARC");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsPPRREG_thenReturnTrue() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("PPRREG");

        // Assert
        assertTrue(result);
    }

    @Test
    void whenConceptoIsInvalid_thenReturnFalse() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("INVALID");

        // Assert
        assertFalse(result);
    }

    @Test
    void whenConceptoIsEmpty_thenReturnFalse() {
        // Act
        boolean result = InputTransactionCodeValidator.conceptoPerValidate("");

        // Assert
        assertFalse(result);
    }

    // Tests para validateCanalConceptoRelation
    @Test
    void whenCanal81AndConceptoCOBPER_thenNoException() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 81, "COBPER")
        );
    }

    @Test
    void whenCanal81AndConceptoNotCOBPER_thenThrowIllegalArgument() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 81, "TRCPRO")
        );
        assertEquals("Canal 81 solo permite concepto COBPER", exception.getMessage());
    }

    @Test
    void whenCanal151AndConceptoCOBPER_thenThrowIllegalArgument() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 151, "COBPER")
        );
        assertEquals("Canal 151 no permite concepto COBPER", exception.getMessage());
    }

    @Test
    void whenCanal151AndConceptoTRCPRO_thenNoException() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 151, "TRCPRO")
        );
    }

    @Test
    void whenCanal151AndConceptoTRCTER_thenNoException() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 151, "TRCTER")
        );
    }

    @Test
    void whenCanal151AndConceptoPPRREG_thenNoException() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 151, "PPRREG")
        );
    }

    @Test
    void whenOtherCanalAndAnyConcept_thenNoException() {
        // Act & Assert - No debe lanzar excepción para otros canales
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 99, "COBPER")
        );
        assertDoesNotThrow(() -> 
            InputTransactionCodeValidator.validateCanalConceptoRelation((short) 200, "TRCPRO")
        );
    }
}
