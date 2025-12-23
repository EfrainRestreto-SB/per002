package pa.davivienda.domain.enums;

/**
 * Tipos de mensajes de auditoría para el sistema de logging funcional.
 * 
 * Cada transacción debe generar exactamente 4 registros:
 * - ENTRADA: Request recibido desde el BUS
 * - TRAMA_OUT: Mensaje transformado hacia AS/400
 * - TRAMA_IN: Respuesta recibida desde AS/400
 * - SALIDA: Response final enviado al BUS
 * - ERROR: Registro adicional en caso de excepción
 */
public enum AuditMessageType {
    /**
     * Request recibido desde el BUS (headers + body)
     */
    ENTRADA,
    
    /**
     * Mensaje transformado hacia AS/400 (query + parámetros)
     */
    TRAMA_OUT,
    
    /**
     * Respuesta recibida desde AS/400
     */
    TRAMA_IN,
    
    /**
     * Response final enviado al BUS (headers + body)
     */
    SALIDA,
    
    /**
     * Registro de error (excepción + stack trace)
     */
    ERROR
}
