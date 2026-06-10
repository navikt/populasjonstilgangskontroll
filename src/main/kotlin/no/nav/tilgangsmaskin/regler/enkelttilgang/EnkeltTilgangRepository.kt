package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.annotation.Timed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

@Timed
interface EnkeltTilgangRepository : JpaRepository<EnkeltTilgangEntity, Long> {

    @Query("""
        SELECT o FROM overstyring o
        WHERE o.navid = :ansattId
          AND o.fnr IN :brukerIds
          AND o.expires > :now
          AND o.created = (
              SELECT MAX(o2.created) FROM overstyring o2
              WHERE o2.navid = :ansattId AND o2.fnr IN :brukerIds
          )
    """)
    fun gjeldende(
            @Param("ansattId") ansattId: String,
            @Param("brukerIds") brukerIds: Set<String>,
            @Param("now") now: Instant = Instant.now()): EnkeltTilgangEntity?

    @Query("""
    SELECT o FROM overstyring o
    WHERE o.navid = :ansattId
      AND o.fnr IN :brukerIds
      AND o.expires > :now
      AND o.created = (
          SELECT MAX(o2.created) FROM overstyring o2
          WHERE o2.fnr = o.fnr AND o2.navid = o.navid
      )
""")
    fun gjeldendeOverstyringer(
        @Param("ansattId") ansattId: String,
        @Param("brukerIds") brukerIds: Set<String>,
        @Param("now") now: Instant = Instant.now()): Set<EnkeltTilgangEntity>

}