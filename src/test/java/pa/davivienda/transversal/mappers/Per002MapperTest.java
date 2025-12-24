package pa.davivienda.transversal.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.models.requests.Per002RequestModel;
import pa.davivienda.domain.models.responses.Per002ResponseModel;

/**
 * Tests unitarios para Per002Mapper (MapStruct).
 * 
 * Verifica el mapeo correcto entre modelos y DTOs:
 * - RequestModel → RequestDto (web → aplicación)
 * - ResponseDto → ResponseModel (aplicación → web)
 */
@QuarkusTest
class Per002MapperTest {

    @Inject
    Per002Mapper mapper;

    // ============== CONSTANTES DE TEST ==============
    private static final String TEST_SESSION_ID = "SESSION123";
    private static final String TEST_SESSION_ID_SPECIAL = "SESSION-123_ABC";
    private static final String TEST_LANGUAGE_SPANISH = "ES";
    private static final String TEST_ORIGIN_WEB = "WEB";
    private static final String TEST_COUNTRY_PANAMA = "PA";
    private static final String TEST_COUNTRY_COSTA_RICA = "CR";
    private static final String TEST_APP_VERSION = "1.0";
    private static final String TEST_APP_VERSION_BETA = "1.0.0-beta";
    private static final String TEST_ID_TYPE_CEDULA = "CED";
    private static final String TEST_ID_NUMBER_PANAMA = "8-111-111";
    private static final String TEST_ID_NUMBER_COSTA_RICA = "1-2345-6789";
    private static final String TEST_TRANSACTION_CODE = "COBPER";
    private static final String TEST_TIMESTAMP = "2025-12-24T10:30:45-06:00";
    private static final String TEST_CURRENCY_USD = "USD";
    private static final String TEST_CURRENCY_EUR = "EUR";
    private static final int TEST_COST_NORMAL = 150;
    private static final int TEST_COST_ZERO = 0;
    private static final int TEST_COST_LARGE = 1_000_000;
    private static final int TEST_COST_NEGATIVE = -150;
    private static final int TEST_COST_EUR = 120;

    // ============== TESTS DE MAPEO REQUEST MODEL → DTO ==============

    @Test
    void whenMappingRequestModel_thenAllFieldsAreMapped() {
        // Given
        Per002RequestModel model = Per002RequestModel.builder()
                .idSesion(TEST_SESSION_ID)
                .codIdioma(TEST_LANGUAGE_SPANISH)
                .valOrigen(TEST_ORIGIN_WEB)
                .codPais(TEST_COUNTRY_PANAMA)
                .valVersionApp(TEST_APP_VERSION)
                .codTipoIdentificacion(TEST_ID_TYPE_CEDULA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_PANAMA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                .build();

        // When
        Per002RequestDto dto = mapper.toRequestDto(model);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getIdSesion()).isEqualTo(TEST_SESSION_ID);
        assertThat(dto.getCodIdioma()).isEqualTo(TEST_LANGUAGE_SPANISH);
        assertThat(dto.getValOrigen()).isEqualTo(TEST_ORIGIN_WEB);
        assertThat(dto.getCodPais()).isEqualTo(TEST_COUNTRY_PANAMA);
        assertThat(dto.getValVersionApp()).isEqualTo(TEST_APP_VERSION);
        assertThat(dto.getCodTipoIdentificacion()).isEqualTo(TEST_ID_TYPE_CEDULA);
        assertThat(dto.getValNumeroIdentificacion()).isEqualTo(TEST_ID_NUMBER_PANAMA);
        assertThat(dto.getCodTipoConcepto()).isEqualTo(TEST_TRANSACTION_CODE);
    }

    @Test
    void whenMappingRequestModelWithNulls_thenNullFieldsAreMapped() {
        // Given
        Per002RequestModel model = Per002RequestModel.builder()
                .codPais(TEST_COUNTRY_PANAMA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_PANAMA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                // Otros campos null
                .build();

        // When
        Per002RequestDto dto = mapper.toRequestDto(model);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getCodPais()).isEqualTo(TEST_COUNTRY_PANAMA);
        assertThat(dto.getValNumeroIdentificacion()).isEqualTo(TEST_ID_NUMBER_PANAMA);
        assertThat(dto.getCodTipoConcepto()).isEqualTo(TEST_TRANSACTION_CODE);
        assertThat(dto.getIdSesion()).isNull();
        assertThat(dto.getCodIdioma()).isNull();
        assertThat(dto.getValOrigen()).isNull();
        assertThat(dto.getValVersionApp()).isNull();
        assertThat(dto.getCodTipoIdentificacion()).isNull();
    }

    @Test
    void whenMappingRequestModelWithEmptyStrings_thenEmptyStringsAreMapped() {
        // Given
        Per002RequestModel model = Per002RequestModel.builder()
                .idSesion("")
                .codIdioma("")
                .valOrigen(TEST_ORIGIN_WEB)
                .codPais(TEST_COUNTRY_PANAMA)
                .valVersionApp("")
                .codTipoIdentificacion(TEST_ID_TYPE_CEDULA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_PANAMA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                .build();

        // When
        Per002RequestDto dto = mapper.toRequestDto(model);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getIdSesion()).isEmpty();
        assertThat(dto.getCodIdioma()).isEmpty();
        assertThat(dto.getValVersionApp()).isEmpty();
        assertThat(dto.getValOrigen()).isEqualTo(TEST_ORIGIN_WEB);
        assertThat(dto.getCodPais()).isEqualTo(TEST_COUNTRY_PANAMA);
    }

    @Test
    void whenMappingRequestModelWithSpecialCharacters_thenCharactersArePreserved() {
        // Given
        Per002RequestModel model = Per002RequestModel.builder()
                .idSesion(TEST_SESSION_ID_SPECIAL)
                .codIdioma(TEST_LANGUAGE_SPANISH)
                .valOrigen(TEST_ORIGIN_WEB)
                .codPais(TEST_COUNTRY_PANAMA)
                .valVersionApp(TEST_APP_VERSION_BETA)
                .codTipoIdentificacion(TEST_ID_TYPE_CEDULA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_PANAMA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                .build();

        // When
        Per002RequestDto dto = mapper.toRequestDto(model);

        // Then
        assertThat(dto.getIdSesion()).isEqualTo(TEST_SESSION_ID_SPECIAL);
        assertThat(dto.getValVersionApp()).isEqualTo(TEST_APP_VERSION_BETA);
    }

    @Test
    void whenMappingRequestModelWithDifferentCountries_thenCountryCodeIsMapped() {
        // Given - Panamá
        Per002RequestModel modelPA = Per002RequestModel.builder()
                .codPais(TEST_COUNTRY_PANAMA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_PANAMA)
                .codTipoIdentificacion(TEST_ID_TYPE_CEDULA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                .build();

        // When
        Per002RequestDto dtoPA = mapper.toRequestDto(modelPA);

        // Then
        assertThat(dtoPA.getCodPais()).isEqualTo(TEST_COUNTRY_PANAMA);

        // Given - Costa Rica
        Per002RequestModel modelCR = Per002RequestModel.builder()
                .codPais(TEST_COUNTRY_COSTA_RICA)
                .valNumeroIdentificacion(TEST_ID_NUMBER_COSTA_RICA)
                .codTipoIdentificacion(TEST_ID_TYPE_CEDULA)
                .codTipoConcepto(TEST_TRANSACTION_CODE)
                .build();

        // When
        Per002RequestDto dtoCR = mapper.toRequestDto(modelCR);

        // Then
        assertThat(dtoCR.getCodPais()).isEqualTo(TEST_COUNTRY_COSTA_RICA);
    }

    // ============== TESTS DE MAPEO RESPONSE DTO → MODEL ==============

    @Test
    void whenMappingResponseDto_thenAllFieldsAreMapped() {
        // Given
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_NORMAL)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model).isNotNull();
        assertThat(model.getFecHoraMovimiento()).isEqualTo(TEST_TIMESTAMP);
        assertThat(model.getCostoDeLaTransaccion()).isEqualTo(TEST_COST_NORMAL);
        assertThat(model.getCodMonedaTransaccion()).isEqualTo(TEST_CURRENCY_USD);
    }

    @Test
    void whenMappingResponseDtoWithZeroCost_thenZeroIsMapped() {
        // Given
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_ZERO)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model.getCostoDeLaTransaccion()).isZero();
    }

    @Test
    void whenMappingResponseDtoWithLargeCost_thenCostIsMapped() {
        // Given - $10,000.00 = 1,000,000 centavos
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_LARGE)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model.getCostoDeLaTransaccion()).isEqualTo(TEST_COST_LARGE);
    }

    @Test
    void whenMappingResponseDtoWithNegativeCost_thenNegativeCostIsMapped() {
        // Given - Costo negativo (devolución/crédito)
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_NEGATIVE)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model.getCostoDeLaTransaccion()).isEqualTo(TEST_COST_NEGATIVE);
    }

    @Test
    void whenMappingResponseDtoWithDifferentCurrencies_thenCurrencyIsMapped() {
        // Given - USD
        Per002ResponseDto dtoUSD = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_NORMAL)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel modelUSD = mapper.toResponseModel(dtoUSD);

        // Then
        assertThat(modelUSD.getCodMonedaTransaccion()).isEqualTo(TEST_CURRENCY_USD);

        // Given - EUR
        Per002ResponseDto dtoEUR = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_EUR)
                .codMonedaTransaccion(TEST_CURRENCY_EUR)
                .build();

        // When
        Per002ResponseModel modelEUR = mapper.toResponseModel(dtoEUR);

        // Then
        assertThat(modelEUR.getCodMonedaTransaccion()).isEqualTo(TEST_CURRENCY_EUR);
    }

    @Test
    void whenMappingResponseDtoWithNullTimestamp_thenNullIsMapped() {
        // Given
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(null)
                .costoDeLaTransaccion(TEST_COST_NORMAL)
                .codMonedaTransaccion(TEST_CURRENCY_USD)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model).isNotNull();
        assertThat(model.getFecHoraMovimiento()).isNull();
        assertThat(model.getCostoDeLaTransaccion()).isEqualTo(TEST_COST_NORMAL);
        assertThat(model.getCodMonedaTransaccion()).isEqualTo(TEST_CURRENCY_USD);
    }

    @Test
    void whenMappingResponseDtoWithNullCurrency_thenNullIsMapped() {
        // Given
        Per002ResponseDto dto = Per002ResponseDto.builder()
                .fecHoraMovimiento(TEST_TIMESTAMP)
                .costoDeLaTransaccion(TEST_COST_NORMAL)
                .codMonedaTransaccion(null)
                .build();

        // When
        Per002ResponseModel model = mapper.toResponseModel(dto);

        // Then
        assertThat(model).isNotNull();
        assertThat(model.getFecHoraMovimiento()).isEqualTo(TEST_TIMESTAMP);
        assertThat(model.getCostoDeLaTransaccion()).isEqualTo(TEST_COST_NORMAL);
        assertThat(model.getCodMonedaTransaccion()).isNull();
    }

    // ============== TESTS DE CASOS EDGE ==============

    @Test
    void whenMappingNullRequestModel_thenReturnsNull() {
        // When
        Per002RequestDto dto = mapper.toRequestDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void whenMappingNullResponseDto_thenReturnsNull() {
        // When
        Per002ResponseModel model = mapper.toResponseModel(null);

        // Then
        assertThat(model).isNull();
    }
}
