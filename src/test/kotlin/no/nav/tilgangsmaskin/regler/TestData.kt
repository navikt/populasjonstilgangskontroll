package no.nav.tilgangsmaskin.regler

import java.util.*
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.Enhetsnummer
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertTilknytning
import no.nav.tilgangsmaskin.regler.brukerids.historiskBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.vanligBrukerId
import no.nav.tilgangsmaskin.regler.diverse.aktørId
import no.nav.tilgangsmaskin.regler.diverse.enhet

object diverse {
    internal val aktørId = AktørId("1234567890123")
    internal val enhet = Enhetsnummer("4242")
}

object brukerids {
    internal val vanligBrukerId = BrukerId("08526835670")
    internal val strengtFortroligBrukerId = BrukerId("08526835671")
    internal val fortroligBrukerId = BrukerId("08526835672")
    internal val ukjentBostedBrukerId = BrukerId("08526835676")
    internal val historiskBrukerId = BrukerId("11111111111")
}

object brukere {
    internal val vanligHistoriskBruker = Bruker(ids(historiskBrukerId), udefinertTilknytning)
    internal val vanligBrukerMedHistoriskIdent =
        Bruker(ids(vanligBrukerId, setOf(vanligHistoriskBruker.brukerId)), udefinertTilknytning)

    private fun ids(id: BrukerId, historiske: Set<BrukerId> = emptySet()) = BrukerIds(id, historiske, aktørId)
}

object grupper {
    internal val strengtFortroligGruppe =
        EntraGruppe(UUID.fromString("5ef775f2-61f8-4283-bf3d-8d03f428aa14"), "Strengt fortrolig gruppe")
    internal val fortroligGruppe =
        EntraGruppe(UUID.fromString("ea930b6b-9397-44d9-b9e6-f4cf527a632a"), "Fortrolig gruppe")
    internal val egenAnsattGruppe =
        EntraGruppe(UUID.fromString("dbe4ad45-320b-4e9a-aaa1-73cca4ee124d"), "egen gruppe")
    internal val annenGruppe = EntraGruppe(UUID.randomUUID(), "Annen gruppe")
    internal val geoUtlandGruppe =
        EntraGruppe(UUID.fromString("de62a4bf-957b-4cde-acdb-6d8bcbf821a0"), "Geo utland gruppe")
    internal val udefinertGruppe =
        EntraGruppe(UUID.fromString("35d9d1ac-7fcb-4a22-9155-e0d1e57898a8"), "Udefinert geo gruppe")
    internal val nasjonalGruppe = EntraGruppe(UUID.fromString("c7107487-310d-4c06-83e0-cf5395dc3be3"), "Nsjonal gruppe")
    internal val enhetGruppe = EntraGruppe(UUID.randomUUID(), "XXX_GEO_${enhet.verdi}")
}

object ansatte {
    internal val ansattId = AnsattId("Z999999")
}