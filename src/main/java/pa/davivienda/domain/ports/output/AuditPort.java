package pa.davivienda.domain.ports.output;

import pa.davivienda.domain.entities.AuditLog;

/**
 * Puerto de salida para auditoría (Arquitectura Hexagonal).
 * 
 * Define el contrato para registrar logs de auditoría funcional.
 * La implementación concreta estará en la capa de persistencia.
 * 
 * Reglas:
 * - No debe romper el flujo principal si falla
 * - Debe implementar retry logic (3 intentos)
 * - Transaccionalmente independiente
 */
public interface AuditPort {
    
    /**
     * Registra un log de auditoría de forma síncrona.
     * 
     * Si falla el registro (después de reintentos), NO debe propagar la excepción.
     * Solo debe loguear el error internamente.
     * 
     * @param auditLog Datos del log a registrar
     */
    void log(AuditLog auditLog);
    
    /**
     * Registra un log de auditoría de forma asíncrona (fire-and-forget).
     * 
     * Útil para no bloquear el flujo principal.
     * 
     * @param auditLog Datos del log a registrar
     */
    void logAsync(AuditLog auditLog);
}
