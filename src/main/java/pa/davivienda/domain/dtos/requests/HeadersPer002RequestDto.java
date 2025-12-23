package pa.davivienda.domain.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HeadersPer002RequestDto {

    public String nombreOperacion;
    public int total;
    public short jornada;
    public short canal;
    public short modoOperacion;
    public String usuario;
    public short perfil;
    public String versionServicio;
    public String idTransaccion;
}
