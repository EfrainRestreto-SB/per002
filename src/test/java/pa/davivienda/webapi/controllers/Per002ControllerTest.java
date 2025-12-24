package pa.davivienda.webapi.controllers;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

/**
 * Tests de integración para Per002Controller.
 * Usa @QuarkusTest para probar el endpoint REST completo.
 * Objetivo: Cubrir controlador, mapper y validadores HTTP.
 * 
 * Nota: Estos tests son de integración real sin mocks,
 * por lo que requieren acceso a la base de datos o
 * configuración de perfil de test con H2/mocks.
 */
@QuarkusTest
class Per002ControllerTest {

    private String validRequestJson;

    @BeforeEach
    void setUp() {
        // Request válido en formato JSON
        validRequestJson = """
                {
                    "idSesion": "SESSION123",
                    "codIdioma": "ES",
                    "valOrigen": "WEB",
                    "codPais": "PA",
                    "valVersionApp": "1.0",
                    "codTipoIdentificacion": "CED",
                    "valNumeroIdentificacion": "8-111-111",
                    "codTipoConcepto": "COBPER"
                }
                """;
    }

    // ============== TESTS DE VALIDACIÓN DE HEADERS ==============

    @Test
    void whenMissingHeaderNombreOperacion_thenReturn400() {
        given()
                .header("Total", "1")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .header("caracterAceptacion", "M")
                .body("validationError", containsString("nombreOperacion"));
    }

    @Test
    void whenMissingHeaderCanal_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "1")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .header("caracterAceptacion", "M")
                .body("validationError", containsString("Canal"));
    }

    @Test
    void whenEmptyHeaderTotal_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("Total"));
    }

    @Test
    void whenInvalidNumericHeaderCanal_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "1")
                .header("Canal", "ABC")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("Canal"));
    }

    @Test
    void whenMissingHeaderIdTransaccion_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("idTransaccion"));
    }

    @Test
    void whenMissingHeaderUsuario_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("usuario"));
    }

    @Test
    void whenMissingHeaderPerfil_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("perfil"));
    }

    @Test
    void whenMissingHeaderJornada_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("jornada"));
    }

    @Test
    void whenInvalidNumericHeaderTotal_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "XYZ")
                .header("jornada", "1")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("Total"));
    }

    @Test
    void whenInvalidNumericHeaderJornada_thenReturn400() {
        given()
                .header("nombreOperacion", "consultaCosto")
                .header("Total", "1")
                .header("jornada", "INVALID")
                .header("Canal", "81")
                .header("modoDeOperacion", "1")
                .header("usuario", "testuser")
                .header("perfil", "1")
                .header("versionServicio", "1.0")
                .header("idTransaccion", "TXN123")
                .contentType("application/json")
                .body(validRequestJson)
        .when()
                .post("/per002/consultaCosto")
        .then()
                .statusCode(400)
                .body("validationError", containsString("jornada"));
    }
}
