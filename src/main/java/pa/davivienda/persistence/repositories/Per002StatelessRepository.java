package pa.davivienda.persistence.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import pa.davivienda.domain.entities.TransactionCost;
import pa.davivienda.domain.entities.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

@ApplicationScoped
public class Per002StatelessRepository {

    @Inject
    EntityManager entityManager;

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
