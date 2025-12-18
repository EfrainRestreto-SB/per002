package org.acme.persistence.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.acme.domain.entities.Cntrlprf;
import org.acme.domain.entities.Cumst;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

@ApplicationScoped
public class Per002StatelessRepository {

    @Inject
    EntityManager entityManager;

    public Cumst findCumstByDocument(String docType, String docNumber) {

        SessionFactory sf = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        try (StatelessSession ss = sf.openStatelessSession()) {

            return ss.createQuery(
                            """
                            FROM Cumst
                            WHERE identificationType = :type
                              AND identificationNumber = :number
                            """,
                            Cumst.class
                    )
                    .setParameter("type", docType)
                    .setParameter("number", docNumber)
                    .setMaxResults(1)   // ✅ ASÍ se limita en JPQL
                    .uniqueResult();
        }
    }

    public Cntrlprf findCntrlprf(String userId, String trxCode) {

        SessionFactory sf = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        try (StatelessSession ss = sf.openStatelessSession()) {

            return ss.createQuery(
                            """
                            FROM Cntrlprf
                            WHERE userId = :userId
                              AND id = :trx
                            """,
                            Cntrlprf.class
                    )
                    .setParameter("userId", userId)
                    .setParameter("trx", trxCode)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
