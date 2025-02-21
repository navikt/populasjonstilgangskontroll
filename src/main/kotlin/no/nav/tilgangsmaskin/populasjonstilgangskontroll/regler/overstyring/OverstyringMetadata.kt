package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import java.time.LocalDate
import kotlin.time.Duration

data class OverstyringMetadata(val begrunnelse: String, val varighet: LocalDate)
