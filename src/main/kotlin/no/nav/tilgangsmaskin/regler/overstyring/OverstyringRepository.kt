package no.nav.tilgangsmaskin.regler.overstyring

import io.micrometer.core.annotation.Timed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Timed
interface OverstyringRepository : JpaRepository<OverstyringEntity, Long> {

    @Query("SELECT o FROM overstyring o WHERE o.navid = :ansattId AND (o.fnr = :brukerId OR o.fnr IN :brukerIds) ORDER BY o.created DESC LIMIT 1")
    fun gjeldendeOverstyring(
            @Param("ansattId") ansattId: String,
            @Param("brukerId") brukerId: String,
            @Param("brukerIds") brukerIds: List<String>): OverstyringEntity?

    @Query("""
    SELECT o FROM overstyring o
    WHERE o.navid = :ansattId
      AND o.fnr IN :brukerIds
      AND o.created = (
          SELECT MAX(o2.created) FROM overstyring o2
          WHERE o2.fnr = o.fnr AND o2.navid = o.navid
      )
""")
    fun gjeldendeOverstyringer(
        @Param("ansattId") ansattId: String,
        @Param("brukerIds") brukerIds: List<String>): List<OverstyringEntity>

    fun antallAktiveOverstyringer(): Long = 0L
}