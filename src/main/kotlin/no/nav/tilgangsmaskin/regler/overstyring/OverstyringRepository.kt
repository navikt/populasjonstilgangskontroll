package no.nav.tilgangsmaskin.regler.overstyring

import io.micrometer.core.annotation.Timed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Timed
interface OverstyringRepository :
    JpaRepository<OverstyringEntity, Long> {

    @Query("SELECT o FROM overstyring o WHERE o.navid = :ansattId AND (o.fnr = :brukerId OR o.fnr IN :brukerIds) ORDER BY o.created DESC LIMIT 1")
    fun gjeldendeOverstyring(
            @Param("ansattId") ansattId: String,
            @Param("brukerId") brukerId: String,
            @Param("brukerIds") brukerIds: List<String>
    ): OverstyringEntity?
}