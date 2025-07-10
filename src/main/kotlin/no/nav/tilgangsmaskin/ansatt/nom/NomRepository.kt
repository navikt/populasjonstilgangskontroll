package no.nav.tilgangsmaskin.ansatt.nom

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface NomRepository : JpaRepository<NomEntity, Long> {
    @Query("SELECT n.fnr FROM NomEntity n WHERE n.navid = :navId AND  n.gyldigtil >= CURRENT_DATE")
    fun ansattBrukerId(navId: String): String?
    fun deleteByGyldigtilBefore(before: Instant = Instant.now()): Int
}
