package pa.davivienda.transversal.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utilidades para el sistema de auditoría.
 * 
 * Proporciona métodos helper para:
 * - Serialización a JSON
 * - Cálculo de hash SHA-256
 * - Manejo de timestamps
 */
public class AuditUtils {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    /**
     * Convierte un objeto a JSON string.
     * 
     * @param obj Objeto a serializar
     * @return JSON string o mensaje de error si falla
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Calcula el hash SHA-256 de un string.
     * 
     * @param input String a hashear
     * @return Hash SHA-256 en formato hexadecimal (64 caracteres)
     */
    public static String calculateSHA256(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre está disponible en JDK 17
            return "";
        }
    }
    
    /**
     * Convierte array de bytes a string hexadecimal.
     * 
     * @param bytes Array de bytes
     * @return String hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Crea un JSON con información de una query SQL.
     * 
     * @param queryName Nombre identificador de la query
     * @param query SQL query
     * @param params Parámetros de la query
     * @return JSON string
     */
    public static String queryToJson(String queryName, String query, Object... params) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"queryName\":\"").append(queryName).append("\",");
        json.append("\"query\":\"").append(escapeJson(query)).append("\",");
        json.append("\"params\":[");
        
        for (int i = 0; i < params.length; i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(params[i]).append("\"");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales para JSON.
     * 
     * @param str String a escapar
     * @return String escapado
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Crea un JSON con información de una excepción.
     * 
     * @param ex Excepción
     * @param context Contexto donde ocurrió
     * @return JSON string
     */
    public static String exceptionToJson(Exception ex, String context) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"context\":\"").append(context).append("\",");
        json.append("\"exception\":\"").append(ex.getClass().getSimpleName()).append("\",");
        json.append("\"message\":\"").append(escapeJson(ex.getMessage())).append("\",");
        json.append("\"stackTrace\":\"").append(escapeJson(getStackTrace(ex))).append("\"");
        json.append("}");
        return json.toString();
    }
    
    /**
     * Obtiene el stack trace completo de una excepción.
     * 
     * @param ex Excepción
     * @return Stack trace como string
     */
    private static String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString()).append("\n");
        
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        
        // Limitar a primeras 20 líneas para no saturar DB
        String fullTrace = sb.toString();
        String[] lines = fullTrace.split("\n");
        if (lines.length > 20) {
            StringBuilder limited = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                limited.append(lines[i]).append("\n");
            }
            limited.append("... (truncated)");
            return limited.toString();
        }
        
        return fullTrace;
    }
}
