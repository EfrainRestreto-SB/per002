package pa.davivienda.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa los costos de transacciones por perfil de cliente.
 * 
 * <p>Mapea la tabla CNTRLPRF (Control Profile) del esquema DAPCYFILES en DB2 i (AS/400).
 * Esta tabla almacena los costos asociados a diferentes tipos de transacciones
 * según el perfil del cliente.</p>
 * 
 * <p>Mapeo de columnas:</p>
 * <ul>
 *   <li>PRFKEY → transactionCode (Código de transacción homologado, ej: 01PAR157)</li>
 *   <li>PRFFA1 → cost (Costo de la transacción en centavos)</li>
 *   <li>PRFFCY → currencyCode (Código de moneda: USD, PAB, etc.)</li>
 *   <li>PRFCUN → customerId (ID del cliente, FK a CUMST.CUSCUN)</li>
 * </ul>
 * 
 * @author Equipo PER002
 * @version 1.0.0
 * @since 2025-12-24
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CNTRLPRF", schema = "DAPCYFILES")
@Entity
public class TransactionCost {

    @Id
    @Column(name = "PRFKEY")
    private String transactionCode;

    @Column(name = "PRFFA1")
    private int cost;

    @Column(name = "PRFFCY")
    private String currencyCode;

    @Column(name = "PRFCUN")
    private String customerId;
}
