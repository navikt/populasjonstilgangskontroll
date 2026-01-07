package no.nav.tilgangsmaskin.ansatt.nom

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.time.Instant.now

interface NomRepository : JpaRepository<NomEntity, Long> {
    fun findFnrByNavidAndGyldigtilGreaterThanEqual(navid: String, gyldigtil: Instant = now()): FnrProjection?
    fun deleteByGyldigtilBefore(before: Instant = now()): Int
}

interface FnrProjection {
    val fnr: String
}

