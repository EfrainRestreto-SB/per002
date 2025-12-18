package org.acme.application.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.validators.InputTransactionCodeValidator;
import org.acme.domain.constants.Constants;
import org.acme.domain.dtos.requests.HeadersPer002RequestDto;
import org.acme.domain.dtos.requests.Per002RequestDto;
import org.acme.domain.dtos.responses.Per002ResponseDto;
import org.acme.domain.entities.Cntrlprf;
import org.acme.domain.entities.Cumst;
import org.acme.domain.interfaces.usecases.Per002UseCase;
import org.acme.persistence.repositories.Per002StatelessRepository;

@ApplicationScoped
public class Per002UseCaseImpl implements Per002UseCase {

    @Inject
    Per002StatelessRepository statelessRepository;

    @Override
    public Per002ResponseDto ConsultaCostoTransaccionPer(
            HeadersPer002RequestDto headers,
            Per002RequestDto request) {

        // 1️⃣ Validación de headers
        if (headers.canal != Constants.CANAL) {
            throw new IllegalArgumentException("Canal inválido");
        }

        // 2️⃣ Homologación de transacción
        String trxCode = InputTransactionCodeValidator.getTransactionCodeHomologate(request.getCodTipoConcepto());

        // 3️⃣ PRIMER SELECT (STATELESS)
        Cumst usuario = statelessRepository.findCumstByDocument(
                request.getCodTipoIdentificacion(),
                request.getValNumeroIdentificacion()
        );

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        // 4️⃣ SEGUNDO SELECT (STATELESS)
        Cntrlprf cntrlprf = statelessRepository.findCntrlprf(usuario.getCumstId(), trxCode);

        if (cntrlprf == null) {
            throw new IllegalArgumentException("Data no encontrada");
        }

        // 5️⃣ Respuesta
        return Per002ResponseDto.builder()
                .codMonedaTransaccion(cntrlprf.getMoneyType())
                .costoDeLaTransaccion(cntrlprf.getMount())
                .build();
    }
}
