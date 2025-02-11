package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.GTRespons
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Kandidat(val ident: Fødselsnummer, val gt: GTRespons, vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav
    override fun toString() = "${javaClass.simpleName} [ident=$ident, gt=$gt,  gruppeKrav=${gruppeKrav.contentToString()}]"
}