package org.acme.webapi.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.acme.domain.dtos.requests.HeadersPer002RequestDto;
import org.acme.domain.dtos.requests.Per002RequestDto;
import org.acme.domain.dtos.responses.Per002ResponseDto;
import org.acme.domain.interfaces.usecases.Per002UseCase;
import org.acme.domain.models.requests.Per002RequestModel;
import org.acme.domain.models.responses.Per002ResponseModel;
import org.acme.transversal.mappers.Per002Mapper;
import org.acme.webapi.validators.InputHeadersPer002Validator;

import java.util.Map;

@Path("per002")
public class Per002Controller {

    @Inject
    private Per002UseCase per002UseCase;

    @Inject
    Per002Mapper per002Mapper;

    @POST
    @Path("/consultaCostos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ConsultaCostoTransaccionPer2() {
        return Response.ok().build();
    }

    @POST
    @Path("/consultaCosto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ConsultaCostoTransaccionPer(
            Per002RequestModel per002RequestModel,
            @Context HttpHeaders httpHeaders
    ) {

        // Obtencion de los headers y su validacion requerida
        HeadersPer002RequestDto headersPer002RequestDto = null;
        try {
            headersPer002RequestDto = InputHeadersPer002Validator.validateInputHeaders(httpHeaders);

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
            Per002ResponseDto per002ResponseDto = per002UseCase.ConsultaCostoTransaccionPer(headersPer002RequestDto, per002RequestDto);
            per002ResponseModel = per002Mapper.toResponseModel(per002ResponseDto);
        }
        catch (Exception ex) {
            Map<String, Object> responseHeaders = Map.of(
                    "nombreOperacion", headersPer002RequestDto.getNombreOperacion(),
                    "total", headersPer002RequestDto.getTotal(),
                    "caracterAceptacion", "M",
                    "ultimoMensaje", 1,
                    "idTransaccion", headersPer002RequestDto.getIdTransaccion(),
                    "codMsgRespuesta", String.valueOf(Response.Status.INTERNAL_SERVER_ERROR),
                    "msgRespuesta", ex.getMessage()
            );
            Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            builder.entity(Map.of("InternalServerError", ex.getMessage()));
            responseHeaders.forEach(builder::header);
            return builder.build();
        }

        // Respuesta con headers
        Map<String, Object> responseHeaders = Map.of(
                "nombreOperacion", headersPer002RequestDto.getNombreOperacion(),
                "total", headersPer002RequestDto.getTotal(),
                "caracterAceptacion", "1.0",
                "ultimoMensaje", "1.0",
                "idTransaccion", headersPer002RequestDto.getIdTransaccion(),
                "codMsgRespuesta", "1.0",
                "msgRespuesta", "1.0"
        );
        Response.ResponseBuilder builder = Response.ok(per002ResponseModel);
        responseHeaders.forEach(builder::header);
        return builder.build();
    }
}
