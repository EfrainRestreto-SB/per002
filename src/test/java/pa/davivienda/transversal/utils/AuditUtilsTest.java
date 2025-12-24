package pa.davivienda.transversal.utils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para AuditUtils.
 * 
 * Verifica funcionalidad de:
 * - Serialización a JSON
 * - Cálculo de hash SHA-256
 * - Helpers para queries y excepciones
 */
class AuditUtilsTest {

    // ============== CONSTANTES DE TEST ==============
    private static final String JSON_EMPTY_OBJECT = "{}";
    private static final String JSON_TEST_STRING = "test string";
    private static final int HASH_LENGTH_SHA256 = 64;
    private static final String HASH_HEX_REGEX = "[a-f0-9]{64}";
    private static final String PARAM_ID_123 = "123";

    // ============== TESTS DE toJson ==============

    @Test
    void whenObjectIsNull_thenReturnsEmptyJsonObject() {
        // When
        String json = AuditUtils.toJson(null);

        // Then
        assertThat(json).isEqualTo(JSON_EMPTY_OBJECT);
    }

    @Test
    void whenObjectIsSimpleMap_thenReturnsValidJson() {
        // Given
        Map<String, String> obj = Map.of(
                "key1", "value1",
                "key2", "value2"
        );

        // When
        String json = AuditUtils.toJson(obj);

        // Then
        assertThat(json).isNotNull();
        assertThat(json).contains("\"key1\"");
        assertThat(json).contains("\"value1\"");
        assertThat(json).contains("\"key2\"");
        assertThat(json).contains("\"value2\"");
    }

    @Test
    void whenObjectIsString_thenReturnsQuotedString() {
        // Given
        String obj = JSON_TEST_STRING;

        // When
        String json = AuditUtils.toJson(obj);

        // Then
        assertThat(json).isEqualTo("\"" + JSON_TEST_STRING + "\"");
    }

    @Test
    void whenObjectIsNumber_thenReturnsNumberAsString() {
        // Given
        Integer obj = 123;

        // When
        String json = AuditUtils.toJson(obj);

        // Then
        assertThat(json).isEqualTo("123");
    }

    // ============== TESTS DE calculateSHA256 ==============

    @Test
    void whenInputIsNull_thenReturnsEmptyString() {
        // When
        String hash = AuditUtils.calculateSHA256(null);

        // Then
        assertThat(hash).isEmpty();
    }

    @Test
    void whenInputIsEmpty_thenReturnsEmptyString() {
        // When
        String hash = AuditUtils.calculateSHA256("");

        // Then
        assertThat(hash).isEmpty();
    }

    @Test
    void whenInputIsValid_thenReturns64CharacterHash() {
        // When
        String hash = AuditUtils.calculateSHA256("test");

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).hasSize(HASH_LENGTH_SHA256); // SHA-256 produce 64 caracteres hex
        assertThat(hash).matches(HASH_HEX_REGEX); // Solo caracteres hexadecimales
    }

    @Test
    void whenInputIsSame_thenHashIsConsistent() {
        // When
        String hash1 = AuditUtils.calculateSHA256("test");
        String hash2 = AuditUtils.calculateSHA256("test");

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void whenInputIsDifferent_thenHashIsDifferent() {
        // When
        String hash1 = AuditUtils.calculateSHA256("test1");
        String hash2 = AuditUtils.calculateSHA256("test2");

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void whenInputHasSpecialCharacters_thenHashIsGenerated() {
        // Given
        String input = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        String hash = AuditUtils.calculateSHA256(input);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).hasSize(HASH_LENGTH_SHA256);
        assertThat(hash).matches(HASH_HEX_REGEX);
    }

    @Test
    void whenInputHasUnicodeCharacters_thenHashIsGenerated() {
        // Given
        String input = "Ñoño con ñ - UTF-8: 中文";

        // When
        String hash = AuditUtils.calculateSHA256(input);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).hasSize(HASH_LENGTH_SHA256);
        assertThat(hash).matches(HASH_HEX_REGEX);
    }

    // ============== TESTS DE queryToJson ==============

    @Test
    void whenQueryWithoutParams_thenReturnsJsonWithEmptyParamsArray() {
        // When
        String json = AuditUtils.queryToJson("findCustomer", "SELECT * FROM customers");

        // Then
        assertThat(json).isNotNull();
        assertThat(json).contains("\"queryName\":\"findCustomer\"");
        assertThat(json).contains("\"query\":\"SELECT * FROM customers\"");
        assertThat(json).contains("\"params\":[]");
    }

    @Test
    void whenQueryWithSingleParam_thenReturnsJsonWithParam() {
        // When
        String json = AuditUtils.queryToJson("findCustomerById", "SELECT * FROM customers WHERE id = ?", PARAM_ID_123);

        // Then
        assertThat(json).contains("\"queryName\":\"findCustomerById\"");
        assertThat(json).contains("\"query\":\"SELECT * FROM customers WHERE id = ?\"");
        assertThat(json).contains("\"params\":[\"" + PARAM_ID_123 + "\"]");
    }

    @Test
    void whenQueryWithMultipleParams_thenReturnsJsonWithAllParams() {
        // When
        String json = AuditUtils.queryToJson(
                "findTransaction",
                "SELECT * FROM transactions WHERE country = ? AND concept = ?",
                "PA",
                "COBPER"
        );

        // Then
        assertThat(json).contains("\"queryName\":\"findTransaction\"");
        assertThat(json).contains("\"params\":[\"PA\",\"COBPER\"]");
    }

    @Test
    void whenQueryHasSpecialCharacters_thenCharactersAreEscaped() {
        // When
        String json = AuditUtils.queryToJson(
                "testQuery",
                "SELECT \"name\" FROM table WHERE value = 'test\nvalue'"
        );

        // Then
        assertThat(json).contains("\\\"name\\\""); // Comillas escapadas
        assertThat(json).contains("\\n"); // Salto de línea escapado
    }

    // ============== TESTS DE exceptionToJson ==============

    @Test
    void whenException_thenReturnsJsonWithExceptionDetails() {
        // Given
        Exception ex = new IllegalArgumentException("Test error message");

        // When
        String json = AuditUtils.exceptionToJson(ex, "validateInput");

        // Then
        assertThat(json).contains("\"context\":\"validateInput\"");
        assertThat(json).contains("\"exception\":\"IllegalArgumentException\"");
        assertThat(json).contains("\"message\":\"Test error message\"");
        assertThat(json).contains("\"stackTrace\":");
    }

    @Test
    void whenExceptionWithNullMessage_thenReturnsJsonWithNullMessage() {
        // Given
        Exception ex = new NullPointerException();

        // When
        String json = AuditUtils.exceptionToJson(ex, "processData");

        // Then
        assertThat(json).contains("\"context\":\"processData\"");
        assertThat(json).contains("\"exception\":\"NullPointerException\"");
        assertThat(json).contains("\"message\":\"\""); // Mensaje null se convierte en ""
    }

    @Test
    void whenExceptionWithSpecialCharactersInMessage_thenCharactersAreEscaped() {
        // Given
        Exception ex = new RuntimeException("Error: \"quoted\" value with\nnewline");

        // When
        String json = AuditUtils.exceptionToJson(ex, "testContext");

        // Then
        assertThat(json).contains("\\\"quoted\\\""); // Comillas escapadas
        assertThat(json).contains("\\n"); // Newline escapado
    }

    @Test
    void whenDifferentExceptionTypes_thenExceptionNameIsCorrect() {
        // Given
        Exception ex1 = new IllegalArgumentException("test");
        Exception ex2 = new NullPointerException("test");
        Exception ex3 = new RuntimeException("test");

        // When
        String json1 = AuditUtils.exceptionToJson(ex1, "context1");
        String json2 = AuditUtils.exceptionToJson(ex2, "context2");
        String json3 = AuditUtils.exceptionToJson(ex3, "context3");

        // Then
        assertThat(json1).contains("\"exception\":\"IllegalArgumentException\"");
        assertThat(json2).contains("\"exception\":\"NullPointerException\"");
        assertThat(json3).contains("\"exception\":\"RuntimeException\"");
    }

    // ============== TESTS DE INTEGRACIÓN ==============

    @Test
    void whenUsedForQueryAudit_thenGeneratesCompleteAuditLog() {
        // Given - Escenario real de auditoría de query
        String queryName = "findTransactionCost";
        String query = "SELECT costo, moneda FROM costos WHERE pais = ? AND concepto = ?";
        String country = "PA";
        String concept = "COBPER";

        // When
        String queryJson = AuditUtils.queryToJson(queryName, query, country, concept);
        String queryHash = AuditUtils.calculateSHA256(queryJson);

        // Then
        assertThat(queryJson).contains(queryName);
        assertThat(queryJson).contains(country);
        assertThat(queryJson).contains(concept);
        assertThat(queryHash).hasSize(HASH_LENGTH_SHA256);
    }

    @Test
    void whenUsedForExceptionAudit_thenGeneratesCompleteErrorLog() {
        // Given - Escenario real de auditoría de error
        try {
            throw new IllegalArgumentException("El usuario no existe");
        } catch (Exception ex) {
            // When
            String exceptionJson = AuditUtils.exceptionToJson(ex, "Per002UseCase.validate");
            String errorHash = AuditUtils.calculateSHA256(exceptionJson);

            // Then
            assertThat(exceptionJson).contains("Per002UseCase.validate");
            assertThat(exceptionJson).contains("El usuario no existe");
            assertThat(errorHash).hasSize(HASH_LENGTH_SHA256);
        }
    }

    @Test
    void whenHashingDifferentDataTypes_thenProducesConsistentHashes() {
        // Given
        String stringData = "test data";
        String jsonData = AuditUtils.toJson(Map.of("key", "value"));
        String queryData = AuditUtils.queryToJson("test", "SELECT * FROM test");

        // When
        String hash1 = AuditUtils.calculateSHA256(stringData);
        String hash2 = AuditUtils.calculateSHA256(jsonData);
        String hash3 = AuditUtils.calculateSHA256(queryData);

        // Then - Todos producen hashes válidos de 64 caracteres
        assertThat(hash1).hasSize(HASH_LENGTH_SHA256).matches(HASH_HEX_REGEX);
        assertThat(hash2).hasSize(HASH_LENGTH_SHA256).matches(HASH_HEX_REGEX);
        assertThat(hash3).hasSize(HASH_LENGTH_SHA256).matches(HASH_HEX_REGEX);
        
        // Y son diferentes entre sí
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(hash2).isNotEqualTo(hash3);
        assertThat(hash1).isNotEqualTo(hash3);
    }
}
