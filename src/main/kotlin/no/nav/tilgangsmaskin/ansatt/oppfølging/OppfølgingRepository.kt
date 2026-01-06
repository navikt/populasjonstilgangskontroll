package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OppfølgingRepository: JpaRepository<OppfølgingEntity, UUID>  {
    fun findByBrukerid(id: String): OppfølgingEntity?
    fun findByAktoerid(id: String): OppfølgingEntity?
}


