package pa.davivienda.domain.interfaces.usecases;

import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;
import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;

public interface Per002UseCase {

    Per002ResponseDto consultaCostoTransaccionPer(HeadersPer002RequestDto headersPer002RequestDto, Per002RequestDto per002RequestDto);
}
