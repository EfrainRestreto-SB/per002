package pa.davivienda.application.usecases;

import java.util.HashMap;
import java.util.Map;

import pa.davivienda.application.validators.InputTransactionCodeValidator;
import pa.davivienda.domain.constants.Constants;
import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;
import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.entities.AuditLog;
import pa.davivienda.domain.entities.TransactionCost;
import pa.davivienda.domain.entities.Customer;
import pa.davivienda.domain.enums.AuditMessageType;
import pa.davivienda.domain.interfaces.usecases.Per002UseCase;
import pa.davivienda.domain.ports.output.AuditPort;
import pa.davivienda.persistence.repositories.Per002StatelessRepository;
import pa.davivienda.transversal.utils.AuditUtils;
import pa.davivienda.transversal.utils.Utilities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Per002UseCaseImpl implements Per002UseCase {

    @Inject
    Per002StatelessRepository statelessRepository;
    
    @Inject
    AuditPort auditPort;

    @Override
    public Per002ResponseDto consultaCostoTransaccionPer(
            HeadersPer002RequestDto headers,
            Per002RequestDto request) {

        String idTransaccion = headers.getIdTransaccion();
        String canal = String.valueOf(headers.getCanal());
        String cun = null; // Se obtendrá después de consultar CUMST
        
        try {
            // 📝 AUDITORÍA 1: ENTRADA
            auditEntrada(idTransaccion, canal, headers, request);
            
            // 1️⃣ Validación de headers
           if (!Constants.CANALES_PERMITIDOS.contains(headers.getCanal())) {
                throw new IllegalArgumentException("Canal inválido. Solo se permiten 81 o 151");
            }

            // 2️⃣ Validación de código de país
            if (!InputTransactionCodeValidator.codPaisValidate(request.getCodPais())) {
                throw new IllegalArgumentException("Código de país no permitido");
            }

            if (!InputTransactionCodeValidator.conceptoPerValidate(request.getCodTipoConcepto())) {
                throw new IllegalArgumentException("Código de concepto no pertenece al catálogo PER");
            }
            InputTransactionCodeValidator.validateCanalConceptoRelation(headers.getCanal(), request.getCodTipoConcepto());
            // 3️⃣ Homologación de transacción
            String trxCode = InputTransactionCodeValidator.getTransactionCodeHomologate(request.getCodTipoConcepto());

            // 📝 AUDITORÍA 2: TRAMA_OUT (Query 1 - Customer)
            auditTramaOut(idTransaccion, canal, "findCustomerByDocument", 
                         request.getCodTipoIdentificacion(), 
                         request.getValNumeroIdentificacion());
            
            // 3️⃣ PRIMER SELECT (STATELESS)
            Customer customer = statelessRepository.findCustomerByDocument(
                    request.getCodTipoIdentificacion(),
                    request.getValNumeroIdentificacion()
            );

            if (customer == null) {
                throw new IllegalArgumentException("El usuario no existe");
            }
            
            // Capturar CUN para auditoría
            cun = customer.getCustomerId();
            
            // 📝 AUDITORÍA 3: TRAMA_IN (Respuesta Query 1)
            auditTramaIn(idTransaccion, canal, cun, "findCustomerByDocument", customer);

            // 📝 AUDITORÍA 4: TRAMA_OUT (Query 2 - TransactionCost)
            auditTramaOut(idTransaccion, canal, "findTransactionCost", customer.getCustomerId(), trxCode);
            
            // 4️⃣ SEGUNDO SELECT (STATELESS)
            TransactionCost transactionCost = statelessRepository.findTransactionCost(customer.getCustomerId(), trxCode);

            if (transactionCost == null) {
                throw new IllegalArgumentException("Data no encontrada");
            }
            
            // 📝 AUDITORÍA 5: TRAMA_IN (Respuesta Query 2)
            auditTramaIn(idTransaccion, canal, cun, "findTransactionCost", transactionCost);

            // 5️⃣ Respuesta
            Per002ResponseDto response = Per002ResponseDto.builder()
                    .fecHoraMovimiento(Utilities.getDateNowFormatted())
                    .codMonedaTransaccion(transactionCost.getCurrencyCode())
                    .costoDeLaTransaccion(transactionCost.getCost())
                    .build();
            
            // 📝 AUDITORÍA 6: SALIDA
            auditSalida(idTransaccion, canal, cun, response);
            
            return response;
            
        } catch (Exception ex) {
            // 📝 AUDITORÍA 7: ERROR
            auditError(idTransaccion, canal, cun, ex);
            throw ex;
        }
    }
    
    /**
     * Audita el request de entrada (ENTRADA).
     */
    private void auditEntrada(String idTransaccion, String canal, 
                             HeadersPer002RequestDto headers, Per002RequestDto request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("headers", headers);
        payload.put("body", request);
        
        String payloadJson = AuditUtils.toJson(payload);
        
        AuditLog log = AuditLog.builder()
                .idTransaccion(idTransaccion)
                .tipoMensaje(AuditMessageType.ENTRADA)
                .logCun(null) // Aún no tenemos el CUN
                .logCanal(canal)
                .loginUser("SYSTEM")
                .payload(payloadJson)
                .payloadHash(AuditUtils.calculateSHA256(payloadJson))
                .estado("OK")
                .build();
        
        auditPort.logAsync(log);
    }
    
    /**
     * Audita una query saliente hacia AS/400 (TRAMA_OUT).
     */
    private void auditTramaOut(String idTransaccion, String canal, 
                              String queryName, Object... params) {
        String query = getQueryByName(queryName);
        String payloadJson = AuditUtils.queryToJson(queryName, query, params);
        
        AuditLog log = AuditLog.builder()
                .idTransaccion(idTransaccion)
                .tipoMensaje(AuditMessageType.TRAMA_OUT)
                .logCun(null)
                .logCanal(canal)
                .loginUser("SYSTEM")
                .payload(payloadJson)
                .payloadHash(AuditUtils.calculateSHA256(payloadJson))
                .estado("OK")
                .build();
        
        auditPort.logAsync(log);
    }
    
    /**
     * Audita una respuesta recibida desde AS/400 (TRAMA_IN).
     */
    private void auditTramaIn(String idTransaccion, String canal, String cun,
                             String queryName, Object result) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("queryName", queryName);
        payload.put("result", result);
        
        String payloadJson = AuditUtils.toJson(payload);
        
        AuditLog log = AuditLog.builder()
                .idTransaccion(idTransaccion)
                .tipoMensaje(AuditMessageType.TRAMA_IN)
                .logCun(cun)
                .logCanal(canal)
                .loginUser("SYSTEM")
                .payload(payloadJson)
                .payloadHash(AuditUtils.calculateSHA256(payloadJson))
                .estado("OK")
                .build();
        
        auditPort.logAsync(log);
    }
    
    /**
     * Audita el response final (SALIDA).
     */
    private void auditSalida(String idTransaccion, String canal, String cun,
                            Per002ResponseDto response) {
        String payloadJson = AuditUtils.toJson(response);
        
        AuditLog log = AuditLog.builder()
                .idTransaccion(idTransaccion)
                .tipoMensaje(AuditMessageType.SALIDA)
                .logCun(cun)
                .logCanal(canal)
                .loginUser("SYSTEM")
                .payload(payloadJson)
                .payloadHash(AuditUtils.calculateSHA256(payloadJson))
                .estado("OK")
                .build();
        
        auditPort.logAsync(log);
    }
    
    /**
     * Audita un error (ERROR).
     */
    private void auditError(String idTransaccion, String canal, String cun, Exception ex) {
        String payloadJson = AuditUtils.exceptionToJson(ex, "ConsultaCostoTransaccionPer");
        
        AuditLog log = AuditLog.builder()
                .idTransaccion(idTransaccion)
                .tipoMensaje(AuditMessageType.ERROR)
                .logCun(cun)
                .logCanal(canal)
                .loginUser("SYSTEM")
                .payload(payloadJson)
                .payloadHash(AuditUtils.calculateSHA256(payloadJson))
                .estado("ERROR")
                .detalleError(ex.getMessage())
                .build();
        
        auditPort.logAsync(log);
    }
    
    /**
     * Retorna el SQL de la query según el nombre.
     */
    private String getQueryByName(String queryName) {
        return switch (queryName) {
            case "findCumstByDocument" -> 
                "FROM Cumst WHERE identificationType = :type AND identificationNumber = :number";
            case "findCntrlprf" -> 
                "FROM Cntrlprf WHERE userId = :userId AND id = :trx";
            default -> queryName;
        };
    }
}
