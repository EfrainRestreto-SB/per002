package pa.davivienda.domain.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HeadersPer002RequestDto {

    private String nombreOperacion;
    private int total;
    private short jornada;
    private short canal;
    private short modoOperacion;
    private String usuario;
    private short perfil;
    private String versionServicio;
    private String idTransaccion;
}
