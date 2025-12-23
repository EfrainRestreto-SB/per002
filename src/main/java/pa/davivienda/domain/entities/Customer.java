package pa.davivienda.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CUMST", schema = "DAPCYFILES")
@Entity
public class Customer {

    @Id
    @Column(name = "CUSCUN")
    private String customerId;

    @Column(name = "CUSTID")
    private String identificationType;

    @Column(name = "CUSIDN")
    private String identificationNumber;
}