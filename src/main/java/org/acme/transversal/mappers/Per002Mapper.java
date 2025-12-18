package org.acme.transversal.mappers;

import org.acme.domain.dtos.requests.Per002RequestDto;
import org.acme.domain.dtos.responses.Per002ResponseDto;
import org.acme.domain.models.requests.Per002RequestModel;
import org.acme.domain.models.responses.Per002ResponseModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface Per002Mapper {

    Per002RequestDto toRequestDto(Per002RequestModel per002RequestModel);

    Per002ResponseModel toResponseModel(Per002ResponseDto per002ResponseDto);
}
