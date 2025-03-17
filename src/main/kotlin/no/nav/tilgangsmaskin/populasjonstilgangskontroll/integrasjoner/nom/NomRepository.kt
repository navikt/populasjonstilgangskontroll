package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface NomRepository : JpaRepository<NomEntity, Long> {
  @Query("SELECT n.fnr FROM NomEntity n WHERE n.navid = :navId  AND n.gyldigtil IS NULL OR n.gyldigtil >= CURRENT_DATE")
  fun finnGyldigAnsattFnr(navId: String): String?

  fun  deleteByGyldigtilBefore(before: Instant)
  fun findByNavid(navId: String): NomEntity?
}