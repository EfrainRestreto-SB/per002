package org.acme.persistence.adapters;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

import org.acme.domain.entities.AuditLog;
import org.acme.domain.ports.output.AuditPort;
import org.acme.transversal.utils.AuditUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * Adapter JDBC para auditoría usando StatelessSession de Hibernate.
 * 
 * Implementación del puerto AuditPort siguiendo arquitectura hexagonal.
 * 
 * Características:
 * - Usa StatelessSession (como Per002StatelessRepository)
 * - Retry automático (3 intentos)
 * - Transaccionalmente independiente
 * - No propaga excepciones al flujo principal
 */
@ApplicationScoped
public class AuditAdapterJdbc implements AuditPort {
    
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 100;
    
    @Inject
    EntityManager entityManager;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void log(AuditLog auditLog) {
        try {
            insertWithRetry(auditLog, MAX_RETRIES);
        } catch (Exception e) {
            // No propagar excepción - solo loguear
            Log.errorf("Failed to insert audit log after %d retries: %s", 
                      MAX_RETRIES, e.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void logAsync(AuditLog auditLog) {
        CompletableFuture.runAsync(() -> log(auditLog));
    }
    
    /**
     * Inserta el log con retry logic.
     * 
     * @param auditLog Log a insertar
     * @param retriesLeft Intentos restantes
     * @throws Exception Si falla después de todos los reintentos
     */
    private void insertWithRetry(AuditLog auditLog, int retriesLeft) throws Exception {
        try {
            insertAuditLog(auditLog);
        } catch (Exception e) {
            if (retriesLeft > 1) {
                // Calcular delay exponencial
                long delay = INITIAL_RETRY_DELAY_MS * (MAX_RETRIES - retriesLeft + 1);
                
                Log.warnf("Audit insert failed, retrying in %dms. Retries left: %d. Error: %s",
                         delay, retriesLeft - 1, e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                
                insertWithRetry(auditLog, retriesLeft - 1);
            } else {
                // Último intento falló
                throw e;
            }
        }
    }
    
    /**
     * Realiza el INSERT en PERUSRLIB.AUDIT_LOGS usando StatelessSession.
     * 
     * @param auditLog Log a insertar
     */
    private void insertAuditLog(AuditLog auditLog) {
        SessionFactory sf = entityManager
                .unwrap(Session.class)
                .getSessionFactory();
        
        try (StatelessSession ss = sf.openStatelessSession()) {
            
            // Iniciar transacción independiente
            ss.beginTransaction();
            
            try {
                // SQL INSERT
                String sql = """
                    INSERT INTO PERUSRLIB.AUDIT_LOGS (
                        ID_TRANSACCION,
                        TIPO_MENSAJE,
                        LOG_CUN,
                        LOG_CANAL,
                        LOGIN_USER,
                        TS,
                        PAYLOAD,
                        PAYLOAD_HASH,
                        ESTADO,
                        DETALLE_ERROR,
                        ORIGEN,
                        SERVICIO,
                        CREATED_BY
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
                
                // Calcular hash si no está calculado
                String payloadHash = auditLog.getPayloadHash();
                if (payloadHash == null && auditLog.getPayload() != null) {
                    payloadHash = AuditUtils.calculateSHA256(auditLog.getPayload());
                }
                
                // Ejecutar INSERT
                ss.createNativeMutationQuery(sql)
                    .setParameter(1, auditLog.getIdTransaccion())
                    .setParameter(2, auditLog.getTipoMensaje().name())
                    .setParameter(3, auditLog.getLogCun())
                    .setParameter(4, auditLog.getLogCanal())
                    .setParameter(5, auditLog.getLoginUser())
                    .setParameter(6, Timestamp.from(auditLog.getTimestamp()))
                    .setParameter(7, auditLog.getPayload())
                    .setParameter(8, payloadHash)
                    .setParameter(9, auditLog.getEstado())
                    .setParameter(10, auditLog.getDetalleError())
                    .setParameter(11, auditLog.getOrigen())
                    .setParameter(12, auditLog.getServicio())
                    .setParameter(13, auditLog.getCreatedBy())
                    .executeUpdate();
                
                // Commit transacción
                ss.getTransaction().commit();
                
                Log.debugf("Audit log inserted: type=%s, trx=%s", 
                          auditLog.getTipoMensaje(), auditLog.getIdTransaccion());
                
            } catch (Exception e) {
                // Rollback en caso de error
                if (ss.getTransaction().isActive()) {
                    ss.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}
