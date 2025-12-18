package org.acme.domain.interfaces.usecases;

import org.acme.domain.dtos.requests.HeadersPer002RequestDto;
import org.acme.domain.dtos.requests.Per002RequestDto;
import org.acme.domain.dtos.responses.Per002ResponseDto;

public interface Per002UseCase {

    Per002ResponseDto ConsultaCostoTransaccionPer(HeadersPer002RequestDto headersPer002RequestDto, Per002RequestDto per002RequestDto);
}
