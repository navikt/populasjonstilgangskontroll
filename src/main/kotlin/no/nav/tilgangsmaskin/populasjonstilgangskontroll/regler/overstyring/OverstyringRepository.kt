package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OverstyringRepository : JpaRepository<OverstyringEntity, Long> {

    @Query("SELECT o FROM overstyring o WHERE o.navid = :ansattId AND o.fnr = :brukerId ORDER BY o.created DESC limit 1")
    fun finnGjeldendeOverstyring(@Param("ansattId") ansattId: String, @Param("brukerId") brukerId: String): OverstyringEntity?
}