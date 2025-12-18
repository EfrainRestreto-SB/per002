package org.acme.webapi.validators;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.HttpHeaders;
import org.acme.domain.dtos.requests.HeadersPer002RequestDto;
import org.acme.transversal.utils.Utilities;

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

        return HeadersPer002RequestDto.builder()
                .nombreOperacion(nombreOperacion)
                .total(Integer.parseInt(total))
                .jornada(Short.parseShort(jornada))
                .canal(Short.parseShort(canal))
                .modoOperacion(Short.parseShort(modoDeOperacion))
                .usuario(usuario)
                .perfil(Short.parseShort(perfil))
                .versionServicio(versionServicio)
                .idTransaccion(idTransaccion)
                .build();
    }
}
