package org.acme.application.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.validators.InputTransactionCodeValidator;
import org.acme.domain.constants.Constants;
import org.acme.domain.dtos.requests.HeadersPer002RequestDto;
import org.acme.domain.dtos.requests.Per002RequestDto;
import org.acme.domain.dtos.responses.Per002ResponseDto;
import org.acme.domain.entities.AuditLog;
import org.acme.domain.entities.Cntrlprf;
import org.acme.domain.entities.Cumst;
import org.acme.domain.enums.AuditMessageType;
import org.acme.domain.interfaces.usecases.Per002UseCase;
import org.acme.domain.ports.output.AuditPort;
import org.acme.persistence.repositories.Per002StatelessRepository;
import org.acme.transversal.utils.AuditUtils;
import org.acme.transversal.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class Per002UseCaseImpl implements Per002UseCase {

    @Inject
    Per002StatelessRepository statelessRepository;
    
    @Inject
    AuditPort auditPort;

    @Override
    public Per002ResponseDto ConsultaCostoTransaccionPer(
            HeadersPer002RequestDto headers,
            Per002RequestDto request) {

        String idTransaccion = headers.getIdTransaccion();
        String canal = String.valueOf(headers.getCanal());
        String cun = null; // Se obtendrá después de consultar CUMST
        
        try {
            // 📝 AUDITORÍA 1: ENTRADA
            auditEntrada(idTransaccion, canal, headers, request);
            
            // 1️⃣ Validación de headers
            if (headers.canal != Constants.CANAL) {
                throw new IllegalArgumentException("Canal inválido");
            }

            // 2️⃣ Homologación de transacción
            String trxCode = InputTransactionCodeValidator.getTransactionCodeHomologate(request.getCodTipoConcepto());

            // 📝 AUDITORÍA 2: TRAMA_OUT (Query 1 - CUMST)
            auditTramaOut(idTransaccion, canal, "findCumstByDocument", 
                         request.getCodTipoIdentificacion(), 
                         request.getValNumeroIdentificacion());
            
            // 3️⃣ PRIMER SELECT (STATELESS)
            Cumst usuario = statelessRepository.findCumstByDocument(
                    request.getCodTipoIdentificacion(),
                    request.getValNumeroIdentificacion()
            );

            if (usuario == null) {
                throw new IllegalArgumentException("El usuario no existe");
            }
            
            // Capturar CUN para auditoría
            cun = usuario.getCumstId();
            
            // 📝 AUDITORÍA 3: TRAMA_IN (Respuesta Query 1)
            auditTramaIn(idTransaccion, canal, cun, "findCumstByDocument", usuario);

            // 📝 AUDITORÍA 4: TRAMA_OUT (Query 2 - CNTRLPRF)
            auditTramaOut(idTransaccion, canal, "findCntrlprf", usuario.getCumstId(), trxCode);
            
            // 4️⃣ SEGUNDO SELECT (STATELESS)
            Cntrlprf cntrlprf = statelessRepository.findCntrlprf(usuario.getCumstId(), trxCode);

            if (cntrlprf == null) {
                throw new IllegalArgumentException("Data no encontrada");
            }
            
            // 📝 AUDITORÍA 5: TRAMA_IN (Respuesta Query 2)
            auditTramaIn(idTransaccion, canal, cun, "findCntrlprf", cntrlprf);

            // 5️⃣ Respuesta
            Per002ResponseDto response = Per002ResponseDto.builder()
                    .fecHoraMovimiento(Utilities.getDateNowFormatted())
                    .codMonedaTransaccion(cntrlprf.getMoneyType())
                    .costoDeLaTransaccion(cntrlprf.getMount())
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
