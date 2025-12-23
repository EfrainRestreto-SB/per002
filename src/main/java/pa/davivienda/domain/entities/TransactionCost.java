package pa.davivienda.domain.entities;

import jakarta.persistence.*;
import lombok.*;

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
