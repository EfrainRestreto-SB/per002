package org.acme.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CNTRLPRF", schema = "DAPCYFILES")
@Entity
public class Cntrlprf {

    @Id
    @Column(name = "PRFKEY")
    private String id;

    @Column(name = "PRFFA1")
    private int mount;

    @Column(name = "PRFFCY")
    private String moneyType;

    @Column(name = "PRFCUN")
    private String userId;
}
