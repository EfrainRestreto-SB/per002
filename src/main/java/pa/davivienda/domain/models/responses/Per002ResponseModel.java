package pa.davivienda.domain.models.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Per002ResponseModel {

    private String fecHoraMovimiento;
    private int costoDeLaTransaccion;
    private String codMonedaTransaccion;
}
