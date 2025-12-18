package org.acme.application.validators;

import java.util.List;
import java.util.Map;

public class InputTransactionCodeValidator {

    public static String getTransactionCodeHomologate(String transactionCode) {

        Map<String, String> homolgatedData = Map.of(
                "TRCPRO", "01PAR153",
                "TRCTER", "01PAR154"
        );

        if(homolgatedData.get(transactionCode) == null || homolgatedData.get(transactionCode).isBlank())
            throw new IllegalArgumentException("Codigo de transaccion no permitido");

        return homolgatedData.get(transactionCode);
    }

    public static boolean codPaisValidate(String codPais) {

        List<String> codPaises = List.of("CR", "CO", "SV", "HN", "PA", "US");

        return codPaises.contains(codPais);
    }
}
