package pa.davivienda.webapi.validators;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.HttpHeaders;
import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;
import pa.davivienda.transversal.utils.Utilities;

/**
 * Validador de headers HTTP requeridos por el BUS para el servicio PER002.
 * 
 * <p>Esta clase realiza validación exhaustiva de los 9 headers obligatorios necesarios
 * para procesar una solicitud de consulta de costos. Las validaciones incluyen:</p>
 * <ul>
 *   <li>Presencia de todos los headers requeridos</li>
 *   <li>Validación de valores vacíos</li>
 *   <li>Validación de formato numérico para: Total, jornada, Canal, modoDeOperacion, perfil</li>
 *   <li>Manejo de excepciones con mensajes claros</li>
 * </ul>
 * 
 * <p>Headers validados:</p>
 * <ul>
 *   <li>nombreOperacion - Nombre de la operación invocada</li>
 *   <li>Total - Cantidad de registros (Integer)</li>
 *   <li>jornada - Identificador de jornada (Integer)</li>
 *   <li>Canal - Canal de origen: 81 o 151 (Short)</li>
 *   <li>modoDeOperacion - Modo de operación (Short)</li>
 *   <li>usuario - Usuario que realiza la operación</li>
 *   <li>perfil - Perfil del usuario (Integer)</li>
 *   <li>versionServicio - Versión del servicio</li>
 *   <li>idTransaccion - Identificador único de la transacción</li>
 * </ul>
 * 
 * @author Equipo PER002
 * @version 1.0.0
 * @since 2025-12-24
 * @see HeadersPer002RequestDto
 */
public class InputHeadersPer002Validator {

    public static HeadersPer002RequestDto validateInputHeaders(HttpHeaders httpHeaders) {

        String nombreOperacion = Utilities.getSpecificHeader(httpHeaders, "nombreOperacion")
                .orElseThrow(() -> new BadRequestException("Missing header: nombreOperacion"));

        String total = Utilities.getSpecificHeader(httpHeaders, "Total")
                .orElseThrow(() -> new BadRequestException("Missing header: Total"));

        String jornada = Utilities.getSpecificHeader(httpHeaders, "jornada")
                .orElseThrow(() -> new BadRequestException("Missing header: jornada"));

        String canal = Utilities.getSpecificHeader(httpHeaders, "Canal")
                .orElseThrow(() -> new BadRequestException("Missing header: Canal"));

        String modoDeOperacion  = Utilities.getSpecificHeader(httpHeaders, "modoDeOperacion")
                .orElseThrow(() -> new BadRequestException("Missing header: modoDeOperacion"));

        String usuario = Utilities.getSpecificHeader(httpHeaders, "usuario")
                .orElseThrow(() -> new BadRequestException("Missing header: usuario"));

        String perfil = Utilities.getSpecificHeader(httpHeaders, "perfil")
                .orElseThrow(() -> new BadRequestException("Missing header: perfil"));

        String versionServicio = Utilities.getSpecificHeader(httpHeaders, "versionServicio")
                .orElseThrow(() -> new BadRequestException("Missing header: versionServicio"));

        String idTransaccion = Utilities.getSpecificHeader(httpHeaders, "idTransaccion")
                .orElseThrow(() -> new BadRequestException("Missing header: idTransaccion"));

        // Validación de valores vacíos antes del parseo
        if (total.trim().isEmpty()) {
            throw new BadRequestException("Header 'Total' no puede estar vacío");
        }
        if (jornada.trim().isEmpty()) {
            throw new BadRequestException("Header 'jornada' no puede estar vacío");
        }
        if (canal.trim().isEmpty()) {
            throw new BadRequestException("Header 'Canal' no puede estar vacío");
        }
        if (modoDeOperacion.trim().isEmpty()) {
            throw new BadRequestException("Header 'modoDeOperacion' no puede estar vacío");
        }
        if (perfil.trim().isEmpty()) {
            throw new BadRequestException("Header 'perfil' no puede estar vacío");
        }

        // Parseo seguro después de validación
        int totalParsed;
        short jornadaParsed;
        short canalParsed;
        short modoOperacionParsed;
        short perfilParsed;
        
        try {
            totalParsed = Integer.parseInt(total);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Header 'Total' debe ser un número entero válido");
        }
        
        try {
            jornadaParsed = Short.parseShort(jornada);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Header 'jornada' debe ser un número válido");
        }
        
        try {
            canalParsed = Short.parseShort(canal);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Header 'Canal' debe ser un número válido");
        }
        
        try {
            modoOperacionParsed = Short.parseShort(modoDeOperacion);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Header 'modoDeOperacion' debe ser un número válido");
        }
        
        try {
            perfilParsed = Short.parseShort(perfil);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Header 'perfil' debe ser un número válido");
        }

        return HeadersPer002RequestDto.builder()
                .nombreOperacion(nombreOperacion)
                .total(totalParsed)
                .jornada(jornadaParsed)
                .canal(canalParsed)
                .modoOperacion(modoOperacionParsed)
                .usuario(usuario)
                .perfil(perfilParsed)
                .versionServicio(versionServicio)
                .idTransaccion(idTransaccion)
                .build();
    }
}
