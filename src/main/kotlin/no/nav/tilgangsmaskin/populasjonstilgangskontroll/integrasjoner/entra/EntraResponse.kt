package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import java.util.*

data class EntraResponse(val oid: UUID, val grupper: List<EntraGruppe>)