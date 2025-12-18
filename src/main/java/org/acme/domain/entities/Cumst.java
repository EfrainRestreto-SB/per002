package org.acme.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CUMST", schema = "DAPCYFILES")
@Entity
public class Cumst {

    @Id
    @Column(name = "CUSCUN")
    private String cumstId;

    @Column(name = "CUSTID")
    private String identificationType;

    @Column(name = "CUSIDN")
    private String identificationNumber;
}