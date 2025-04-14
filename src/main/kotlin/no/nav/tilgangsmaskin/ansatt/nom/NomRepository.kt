package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Timed
interface NomRepository : JpaRepository<NomEntity, Long>, NomRepositoryCustom {
    @Query("SELECT n.fnr FROM NomEntity n WHERE n.navid = :navId AND  n.gyldigtil >= CURRENT_DATE")
    fun ansattFødselsnummer(navId: String): String?
    fun deleteByGyldigtilBefore(before: Instant = Instant.now()): Int
    fun findByNavid(navId: String): NomEntity?
}

@Repository
class NomRepositoryCustomImpl : NomRepositoryCustom {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun upsertAndReturnId(navid: String, fnr: String, startdato: Instant, gyldigtil: Instant): Long {
        val query = """
            INSERT INTO ansatte (navid, fnr, startdato, gyldigtil, created, updated)
            VALUES (:navid, :fnr, :startdato, :gyldigtil, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (navid)
            DO UPDATE SET
                fnr = EXCLUDED.fnr,
                startdato = EXCLUDED.startdato,
                gyldigtil = EXCLUDED.gyldigtil,
                updated = CURRENT_TIMESTAMP
            RETURNING id
        """
        return entityManager.createNativeQuery(query)
            .setParameter("navid", navid)
            .setParameter("fnr", fnr)
            .setParameter("startdato", startdato)
            .setParameter("gyldigtil", gyldigtil)
            .singleResult as Long
    }
}

interface NomRepositoryCustom {
    fun upsertAndReturnId(navid: String, fnr: String, startdato: Instant, gyldigtil: Instant): Long
}