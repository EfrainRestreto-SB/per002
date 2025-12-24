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
 * Entidad que representa el maestro de clientes (Customer Master).
 * 
 * <p>Mapea la tabla CUMST del esquema DAPCYFILES en DB2 i (AS/400).
 * Esta tabla contiene la información básica de identificación de los clientes
 * del banco.</p>
 * 
 * <p>Mapeo de columnas:</p>
 * <ul>
 *   <li>CUSCUN → customerId (ID único del cliente)</li>
 *   <li>CUSTID → identificationType (Tipo: C=Cédula, P=Pasaporte, etc.)</li>
 *   <li>CUSIDN → identificationNumber (Número de documento)</li>
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