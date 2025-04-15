package no.nav.tilgangsmaskin.ansatt.nom

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import org.springframework.stereotype.Component
import java.time.Instant


@Component
@ConditionalOnGCP
class NomJPAAdapter(val repo: NomRepository, @PersistenceContext protected val entityManager: EntityManager) {

    fun ryddOpp() = repo.deleteByGyldigtilBefore()

    fun upsert(data: NomAnsattData) =
        with(data) {
            upsert(ansattId, brukerId, gyldighet.start.toInstant(), gyldighet.endInclusive.toInstant())
        }

    private fun upsert(ansattId: AnsattId, ansattFnr: BrukerId, start: Instant, slutt: Instant): Long {
        val query = """
            INSERT INTO Ansatte (navid, fnr, startdato, gyldigtil, created, updated)
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
            .setParameter("navid", ansattId.verdi)
            .setParameter("fnr", ansattFnr.verdi)
            .setParameter("startdato", start)
            .setParameter("gyldigtil", slutt)
            .singleResult as Long
    }

    fun fnrForAnsatt(ansattId: String) = repo.ansattFødselsnummer(ansattId)?.let { BrukerId(it) }

}


