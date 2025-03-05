package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.data.jpa.repository.JpaRepository

interface NomRepository : JpaRepository<NomEntity, Long> {
  fun findByNavid(navId: String): NomEntity?
}