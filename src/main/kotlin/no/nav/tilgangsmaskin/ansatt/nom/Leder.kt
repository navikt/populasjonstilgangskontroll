package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer

data class Leder(
    val epost: String,
    val navident: AnsattId,
    val visningsnavn: String,
)

data class Ledere(val ressurs: Leder)

data class NomRessurs(
    val navident: AnsattId,
    val visningsnavn: String,
    val orgTilknytninger: Set<OrgTilknytning>,
)

data class OrgTilknytning(
    val orgEnhet: OrgEnhet,
    val erDagligOppfolging: Boolean,
)

data class OrgEnhet(
    val id: String,
    val navn: String,
    val ledere: Set<Ledere>,
)