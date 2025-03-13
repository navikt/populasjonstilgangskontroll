package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OverstyringRepository : JpaRepository<OverstyringEntity, Long> {

    @Query("SELECT o FROM overstyring o WHERE o.navid = :ansattId AND (o.fnr = :brukerId OR o.fnr IN :brukerIds) ORDER BY o.created DESC LIMIT 1")
    fun finnGjeldendeOverstyring(@Param("ansattId") ansattId: String, @Param("brukerId") brukerId: String, @Param("brukerIds") brukerIds: List<String>): OverstyringEntity?
}