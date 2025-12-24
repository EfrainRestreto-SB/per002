package pa.davivienda.webapi.controllers;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pa.davivienda.domain.dtos.requests.HeadersPer002RequestDto;
import pa.davivienda.domain.dtos.requests.Per002RequestDto;
import pa.davivienda.domain.dtos.responses.Per002ResponseDto;
import pa.davivienda.domain.interfaces.usecases.Per002UseCase;
import pa.davivienda.domain.models.requests.Per002RequestModel;
import pa.davivienda.domain.models.responses.Per002ResponseModel;
import pa.davivienda.transversal.mappers.Per002Mapper;
import pa.davivienda.webapi.validators.InputHeadersPer002Validator;

@Path("per002")
public class Per002Controller {

    @Inject
    private Per002UseCase per002UseCase;

    @Inject
    Per002Mapper per002Mapper;

    @POST
    @Path("/consultaCosto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaCostoTransaccionPer(
            Per002RequestModel per002RequestModel,
            @Context HttpHeaders httpHeaders
    ) {

        // Obtencion de los headers y su validacion requerida
        HeadersPer002RequestDto headers = null;
        try {
            headers = InputHeadersPer002Validator.validateInputHeaders(httpHeaders);

        } catch (BadRequestException brex) {
            Map<String, Object> responseHeaders = Map.of(
                    "caracterAceptacion", "M",
                    "codMsgRespuesta", String.valueOf(Response.Status.BAD_REQUEST),
                    "msgRespuesta", brex.getMessage()
            );
            Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
            builder.entity(Map.of("validationError", brex.getMessage()));
            responseHeaders.forEach(builder::header);
            return builder.build();
        }

        // Mapeo de campos de entrada de Model a Dto
        Per002RequestDto per002RequestDto = per002Mapper.toRequestDto(per002RequestModel);

        // Ejecucion de logica de negocio
        Per002ResponseModel per002ResponseModel;
        try {
            Per002ResponseDto per002ResponseDto = per002UseCase.consultaCostoTransaccionPer(headers, per002RequestDto);
            per002ResponseModel = per002Mapper.toResponseModel(per002ResponseDto);
        }
        catch (IllegalArgumentException iex) {
            // Errores de validación -> 400 Bad Request
            Map<String, Object> responseHeaders = Map.of(
                    "nombreOperacion", headers.getNombreOperacion(),
                    "total", headers.getTotal(),
                    "caracterAceptacion", "M",
                    "ultimoMensaje", 1,
                    "idTransaccion", headers.getIdTransaccion(),
                    "codMsgRespuesta", String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
                    "msgRespuesta", iex.getMessage()
            );
            Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
            builder.entity(Map.of("validationError", iex.getMessage()));
            responseHeaders.forEach(builder::header);
            return builder.build();
        }
        catch (Exception ex) {
            // Errores del servidor -> 500 Internal Server Error
            Map<String, Object> responseHeaders = Map.of(
                    "nombreOperacion", headers.getNombreOperacion(),
                    "total", headers.getTotal(),
                    "caracterAceptacion", "M",
                    "ultimoMensaje", 1,
                    "idTransaccion", headers.getIdTransaccion(),
                    "codMsgRespuesta", String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()),
                    "msgRespuesta", ex.getMessage()
            );
            Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            builder.entity(Map.of("InternalServerError", ex.getMessage()));
            responseHeaders.forEach(builder::header);
            return builder.build();
        }

        // Respuesta con headers
        Map<String, Object> responseHeaders = Map.of(
                "nombreOperacion", headers.getNombreOperacion(),
                "total", headers.getTotal(),
                "caracterAceptacion", "1.0",
                "ultimoMensaje", "1.0",
                "idTransaccion", headers.getIdTransaccion(),
                "codMsgRespuesta", "1.0",
                "msgRespuesta", "1.0"
        );
        Response.ResponseBuilder builder = Response.ok(per002ResponseModel);
        responseHeaders.forEach(builder::header);
        return builder.build();
    }
}
