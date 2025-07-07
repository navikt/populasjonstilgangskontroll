package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import jakarta.persistence.QueryHint
import java.time.Instant

@Timed
interface NomRepository : JpaRepository<NomEntity, Long> {
    @QueryHints(QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("SELECT n.fnr FROM NomEntity n WHERE n.navid = :navId AND  n.gyldigtil >= CURRENT_DATE")
    fun ansattBrukerId(navId: String): String?
    fun deleteByGyldigtilBefore(before: Instant = Instant.now()): Int
}
