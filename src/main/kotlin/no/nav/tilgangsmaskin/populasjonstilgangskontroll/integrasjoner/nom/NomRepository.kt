package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NomRepository : JpaRepository<NomEntity, Long> {
  @Query("SELECT n.fnr FROM NomEntity n WHERE n.gyldigtil IS NULL OR n.gyldigtil >= CURRENT_DATE")
  fun findByNavid(navId: String): NomEntity?
}