package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import org.springframework.data.jpa.repository.JpaRepository

interface OverstyringRepository : JpaRepository<Overstyring, Long> {
}