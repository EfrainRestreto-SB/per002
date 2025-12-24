package pa.davivienda.transversal.mappers;

import org.mapstruct.Mapper;

import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.models.requests.Per002RequestModel;
import pa.davivienda.domain.models.responses.Per002ResponseModel;

/**
 * Mapper para transformación entre modelos y DTOs del servicio PER002.
 * 
 * <p>Utiliza MapStruct para generar automáticamente las implementaciones de mapeo
 * en tiempo de compilación. La configuración {@code componentModel = "cdi"} permite
 * la inyección de dependencias mediante CDI de Quarkus.</p>
 * 
 * <p>Transformaciones soportadas:</p>
 * <ul>
 *   <li>RequestModel → RequestDto (capa web → capa aplicación)</li>
 *   <li>ResponseDto → ResponseModel (capa aplicación → capa web)</li>
 * </ul>
 * 
 * @author Equipo PER002
 * @version 1.0.0
 * @since 2025-12-24
 * @see org.mapstruct.Mapper
 */
@Mapper(componentModel = "cdi")
public interface Per002Mapper {

    Per002RequestDto toRequestDto(Per002RequestModel per002RequestModel);

    Per002ResponseModel toResponseModel(Per002ResponseDto per002ResponseDto);
}
