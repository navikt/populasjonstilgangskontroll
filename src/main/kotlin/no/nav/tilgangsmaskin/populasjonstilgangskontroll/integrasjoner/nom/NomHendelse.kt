package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import java.time.LocalDate

class NomHendelse(ansattId: AnsattId, startdato: LocalDate, sluttdato: LocalDate?, sektor: Sektor)

enum class Sektor {
    NAV_STATLIG, NAV_KOMMUNAL, EKSTERN
}