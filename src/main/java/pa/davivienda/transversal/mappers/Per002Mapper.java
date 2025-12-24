package pa.davivienda.transversal.mappers;

import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.models.requests.Per002RequestModel;
import pa.davivienda.domain.models.responses.Per002ResponseModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface Per002Mapper {

    Per002RequestDto toRequestDto(Per002RequestModel per002RequestModel);

    Per002ResponseModel toResponseModel(Per002ResponseDto per002ResponseDto);
}
