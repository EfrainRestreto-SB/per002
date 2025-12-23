package pa.davivienda.domain.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Per002ResponseDto {

    private String fecHoraMovimiento;
    private int costoDeLaTransaccion;
    private String codMonedaTransaccion;
}
