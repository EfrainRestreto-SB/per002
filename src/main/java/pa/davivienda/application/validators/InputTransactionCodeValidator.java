package pa.davivienda.application.validators;

import java.util.List;
import java.util.Map;

public class InputTransactionCodeValidator {

    public static String getTransactionCodeHomologate(String transactionCode) {

        Map<String, String> homologatedData = Map.of(
                "COBPER", "01PAR157",
                "TRCPRO", "01PAR153",
                "TRCTER", "01PAR154"
            );

        if (homologatedData.get(transactionCode) == null || homologatedData.get(transactionCode).isBlank())
            throw new IllegalArgumentException("Codigo de transaccion no permitido");

        return homologatedData.get(transactionCode);
    }

    public static boolean codPaisValidate(String codPais) {

        List<String> codPaises = List.of("CR", "CO", "SV", "HN", "PA", "US");

        return codPaises.contains(codPais);
    }

    public static boolean conceptoPerValidate(String codTipoConcepto) {
        List<String> conceptosPer = List.of("COBPER", "TRCPRO", "TRCTER", "TRA11R", "TR1VR", "TININD", "TINARC", "PPRREG");
        return conceptosPer.contains(codTipoConcepto);
    }

    public static void validateCanalConceptoRelation(short canal, String concepto) {
    if (canal == 81 && !concepto.equals("COBPER")) {
        throw new IllegalArgumentException("Canal 81 solo permite concepto COBPER");
    }
    if (canal == 151 && concepto.equals("COBPER")) {
        throw new IllegalArgumentException("Canal 151 no permite concepto COBPER");
    }
}
}
