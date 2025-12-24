package pa.davivienda.persistence.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import pa.davivienda.domain.entities.Customer;
import pa.davivienda.domain.entities.TransactionCost;

/**
 * Repositorio para operaciones transaccionales sin caché en DB2 i (AS/400).
 * 
 * <p>Utiliza {@link StatelessSession} de Hibernate para realizar consultas sin
 * mantener estado ni caché de segundo nivel. Este enfoque es ideal para operaciones
 * transaccionales donde:</p>
 * <ul>
 *   <li>No se requiere caché de entidades</li>
 *   <li>Las consultas son puntuales y no repetitivas</li>
 *   <li>Se busca minimizar el uso de memoria</li>
 *   <li>Se necesita máximo rendimiento en lecturas</li>
 * </ul>
 * 
 * <p>Las consultas se realizan contra las tablas del esquema DAPCYFILES:</p>
 * <ul>
 *   <li>CUMST - Customer Master (Maestro de Clientes)</li>
 *   <li>CNTRLPRF - Control Profile (Perfil de Costos de Transacciones)</li>
 * </ul>
 * 
 * @author Equipo PER002
 * @version 1.0.0
 * @since 2025-12-24
 * @see Customer
 * @see TransactionCost
 */
@ApplicationScoped
public class Per002StatelessRepository {

    @Inject
    EntityManager entityManager;

    /**
     * Busca un cliente por tipo y número de documento.
     * 
     * <p>Realiza una consulta directa a la tabla CUMST del AS/400 utilizando
     * StatelessSession para optimizar el rendimiento. La consulta retorna un
     * único resultado o null si no encuentra coincidencias.</p>
     * 
     * @param docType Tipo de identificación (ej: "C" para cédula, "P" para pasaporte)
     * @param docNumber Número de identificación del cliente
     * @return El objeto {@link Customer} encontrado, o null si no existe
     * @throws RuntimeException Si ocurre un error de conexión o consulta a DB2
     */
    public Customer findCustomerByDocument(String docType, String docNumber) {

        SessionFactory sf = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        try (StatelessSession ss = sf.openStatelessSession()) {

            return ss.createQuery(
                            """
                            FROM Customer
                            WHERE identificationType = :type
                              AND identificationNumber = :number
                            """,
                            Customer.class
                    )
                    .setParameter("type", docType)
                    .setParameter("number", docNumber)
                    .setMaxResults(1)   // ✅ ASÍ se limita en JPQL
                    .uniqueResult();
        }
    }

    public TransactionCost findTransactionCost(String customerId, String trxCode) {

        SessionFactory sf = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        try (StatelessSession ss = sf.openStatelessSession()) {

            return ss.createQuery(
                            """
                            FROM TransactionCost
                            WHERE customerId = :customerId
                              AND transactionCode = :trx
                            """,
                            TransactionCost.class
                    )
                    .setParameter("customerId", customerId)
                    .setParameter("trx", trxCode)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
