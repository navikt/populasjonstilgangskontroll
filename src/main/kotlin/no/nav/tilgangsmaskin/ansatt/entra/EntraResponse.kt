package no.nav.tilgangsmaskin.ansatt.entra

import java.util.*

data class EntraResponse(val oid: UUID, val grupper: List<EntraGruppe>)