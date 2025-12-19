package org.acme.transversal.utils;

import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.Session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * Utilidad temporal para verificar la existencia de tablas en DB2
 */
@ApplicationScoped
public class DbTableChecker {
    
    @Inject
    EntityManager entityManager;
    
    public String checkAuditLogsTable() {
        StringBuilder result = new StringBuilder();
        try {
            Session session = entityManager.unwrap(Session.class);
            final StringBuilder[] resultHolder = {new StringBuilder()};
            
            session.doWork(connection -> {
                try (Statement stmt = connection.createStatement()) {
                    String query = """
                        SELECT TABLE_SCHEMA, TABLE_NAME 
                        FROM QSYS2.SYSTABLES 
                        WHERE TABLE_NAME = 'AUDIT_LOGS'
                        """;
                    
                    ResultSet rs = stmt.executeQuery(query);
                    
                    resultHolder[0].append("========================================\n");
                    resultHolder[0].append("BÚSQUEDA DE TABLA AUDIT_LOGS EN DB2:\n");
                    resultHolder[0].append("========================================\n");
                    
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String schema = rs.getString("TABLE_SCHEMA");
                        String tableName = rs.getString("TABLE_NAME");
                        resultHolder[0].append("✅ ENCONTRADA: ").append(schema).append(".").append(tableName).append("\n");
                    }
                    
                    if (!found) {
                        resultHolder[0].append("❌ NO ENCONTRADA: La tabla AUDIT_LOGS no existe en ninguna library\n");
                        resultHolder[0].append("\n");
                        resultHolder[0].append("Debes crear la tabla ejecutando:\n");
                        resultHolder[0].append("sql/create_audit_table.sql\n");
                    }
                    
                    resultHolder[0].append("========================================\n");
                    rs.close();
                }
            });
            
            result.append(resultHolder[0]);
        } catch (Exception e) {
            result.append("Error verificando tabla: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        String finalResult = result.toString();
        System.out.println(finalResult);
        return finalResult;

    }
}
