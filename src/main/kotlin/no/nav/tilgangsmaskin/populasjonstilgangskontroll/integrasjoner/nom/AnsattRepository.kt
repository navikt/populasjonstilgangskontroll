package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.data.jpa.repository.JpaRepository

interface AnsattRepository : JpaRepository<AnsattEntity, Long> {
  fun findByNavid(navId: String): AnsattEntity?
}