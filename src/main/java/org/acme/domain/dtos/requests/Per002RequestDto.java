package org.acme.domain.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Per002RequestDto {

    private String idSesion;
    private String codIdioma;
    private String valOrigen;
    private String codPais;
    private String valVersionApp;
    private String codTipoIdentificacion;
    private String valNumeroIdentificacion;
    private String codTipoConcepto;
}
