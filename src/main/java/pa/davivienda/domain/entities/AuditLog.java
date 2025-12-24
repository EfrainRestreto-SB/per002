package pa.davivienda.domain.entities;

import java.time.Instant;

import pa.davivienda.domain.enums.AuditMessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad de auditoría para logging funcional.
 * 
 * Representa un registro en la tabla PRESURLIB.AUDIT_LOGS
 * Inmutable usando Lombok @Builder
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {
    
    /**
     * ID de transacción del BUS (viene en headers)
     */
    private String idTransaccion;
    
    /**
     * Tipo de mensaje: ENTRADA, TRAMA_OUT, TRAMA_IN, SALIDA, ERROR
     */
    private AuditMessageType tipoMensaje;
    
    /**
     * CUN del cliente (CUSCUN de tabla CUMST)
     */
    private String logCun;
    
    /**
     * Canal de origen (BM, BI, etc.)
     */
    private String logCanal;
    
    /**
     * Usuario que ejecuta la operación
     */
    private String loginUser;
    
    /**
     * Timestamp del evento
     */
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    /**
     * Payload en formato JSON
     */
    private String payload;
    
    /**
     * Hash SHA-256 del payload
     */
    private String payloadHash;
    
    /**
     * Estado: OK / ERROR
     */
    @Builder.Default
    private String estado = "OK";
    
    /**
     * Detalle del error (si aplica)
     */
    private String detalleError;
    
    /**
     * Origen del microservicio
     */
    @Builder.Default
    private String origen = "PER002";
    
    /**
     * Servicio que genera el registro de auditoría
     */
    @Builder.Default
    private String servicio = "PER002";
    
    /**
     * Sistema/Usuario creador
     */
    @Builder.Default
    private String createdBy = "PER002-SERVICE";
}
