package no.nav.tilgangsmaskin.regler

import java.util.*
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIdentifikatorer
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.Enhetsnummer
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.*
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.utenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.regler.brukere.annenAnsattBruker
import no.nav.tilgangsmaskin.regler.brukere.annenAnsattBrukerMedPartner
import no.nav.tilgangsmaskin.regler.brukere.ansattBruker
import no.nav.tilgangsmaskin.regler.brukerids.annenAnsattBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.annenEnhetBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.ansattBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.egenAnsattFortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.egenAnsattStrengtFortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.enhetBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.fortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.historiskBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.strengtFortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.ukjentBostedBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.utlandBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.vanligBrukerId
import no.nav.tilgangsmaskin.regler.diverse.aktørId
import no.nav.tilgangsmaskin.regler.diverse.enhet
import no.nav.tilgangsmaskin.regler.grupper.annenGruppe
import no.nav.tilgangsmaskin.regler.grupper.egenAnsattGruppe
import no.nav.tilgangsmaskin.regler.grupper.enhetGruppe
import no.nav.tilgangsmaskin.regler.grupper.fortroligGruppe
import no.nav.tilgangsmaskin.regler.grupper.geoUtlandGruppe
import no.nav.tilgangsmaskin.regler.grupper.nasjonalGruppe
import no.nav.tilgangsmaskin.regler.grupper.strengtFortroligGruppe
import no.nav.tilgangsmaskin.regler.grupper.udefinertGruppe

object diverse {
    internal val aktørId = AktørId("1234567890123")
    internal val enhet = Enhetsnummer("4242")
}

object brukerids {
    internal val vanligBrukerId = BrukerId("08526835670")
    internal val strengtFortroligBrukerId = BrukerId("08526835671")
    internal val fortroligBrukerId = BrukerId("08526835672")
    internal val annenAnsattBrukerId = BrukerId("08526835644")
    internal val ansattBrukerId = BrukerId("08526835673")
    internal val egenAnsattStrengtFortroligBrukerId = BrukerId("08526835674")
    internal val egenAnsattFortroligBrukerId = BrukerId("08526835675")
    internal val ukjentBostedBrukerId = BrukerId("08526835676")
    internal val utlandBrukerId = BrukerId("08526835677")
    internal val enhetBrukerId = BrukerId("08526835678")
    internal val annenEnhetBrukerId = BrukerId("08526835679")
    internal val historiskBrukerId = BrukerId("11111111111")
}

object brukere {
    internal val strengtFortroligBruker = bruker(strengtFortroligBrukerId, STRENGT_FORTROLIG)
    internal val strengtFortroligUtlandBruker = bruker(strengtFortroligBrukerId, STRENGT_FORTROLIG_UTLAND)
    internal val fortroligBruker = bruker(fortroligBrukerId, FORTROLIG)
    internal val vanligBruker = bruker(vanligBrukerId)
    internal val vanligHistoriskBruker = Bruker(ids(historiskBrukerId), udefinertTilknytning)
    internal val vanligBrukerMedHistoriskIdent =
        Bruker(ids(vanligBrukerId, setOf(vanligHistoriskBruker.brukerId)), udefinertTilknytning)
    internal val annenAnsattBruker =
        bruker(annenAnsattBrukerId, SKJERMING, familie = Familie(barn = setOf(FamilieMedlem(vanligBrukerId, BARN))))
    internal val annenAnsattBrukerMedPartner =
        bruker(
                annenAnsattBrukerId,
                SKJERMING,
                familie = Familie(partnere = setOf(FamilieMedlem(vanligBrukerId, PARTNER))))
    internal val ansattBruker = bruker(ansattBrukerId, SKJERMING)
    internal val egenAnsattStrengtFortroligBruker =
        bruker(egenAnsattStrengtFortroligBrukerId, STRENGT_FORTROLIG, SKJERMING)

    internal val egenAnsattFortroligBruker = bruker(egenAnsattFortroligBrukerId, FORTROLIG, SKJERMING)
    internal val ukjentBostedBruker = bruker(ukjentBostedBrukerId, UKJENT_BOSTED, tilknytning = UkjentBosted())
    internal val utlandBruker = bruker(utlandBrukerId, UTENLANDSK, tilknytning = utenlandskTilknytning)
    internal val enhetBruker = bruker(enhetBrukerId, tilknytning = KommuneTilknytning(Kommune(enhet.verdi)))
    internal val annenEnhetBruker = bruker(annenEnhetBrukerId, tilknytning = KommuneTilknytning(Kommune("4321")))

    private fun bruker(id: BrukerId,
                       vararg grupper: GlobalGruppe = emptyArray(),
                       tilknytning: GeografiskTilknytning = udefinertTilknytning,
                       familie: Familie = Familie.INGEN) =
        Bruker(ids(id), tilknytning, grupper.toSet(), familie)

    private fun ids(id: BrukerId, historiske: Set<BrukerId> = emptySet()) = BrukerIds(id, aktørId, historiske)
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
    internal val oid = UUID.randomUUID()
    internal val ansattId = AnsattId("Z999999")
    private val aids = AnsattIdentifikatorer(ansattId, oid)
    internal val egenAnsattFortroligAnsatt = ansatt(grupper = setOf(fortroligGruppe, egenAnsattGruppe))
    internal val egenAnsattStrengtFortroligAnsatt = ansatt(grupper = setOf(strengtFortroligGruppe, egenAnsattGruppe))
    internal val strengtFortroligAnsatt = ansatt(gruppe = strengtFortroligGruppe)
    internal val fortroligAnsatt = ansatt(gruppe = fortroligGruppe)
    internal val egenAnsatt = ansatt(gruppe = egenAnsattGruppe)
    internal val egenAnsattMedFamilie = ansatt(annenAnsattBruker, annenGruppe)
    internal val egenAnsattMedPartner = ansatt(annenAnsattBrukerMedPartner, annenGruppe)
    internal val vanligAnsatt = ansatt(gruppe = annenGruppe)
    internal val geoUtlandAnsatt = ansatt(gruppe = geoUtlandGruppe)
    internal val udefinertGeoAnsatt = ansatt(gruppe = udefinertGruppe)
    internal val nasjonalAnsatt = ansatt(gruppe = nasjonalGruppe)
    internal val enhetAnsatt = ansatt(gruppe = enhetGruppe)

    internal fun ansatt(bruker: Bruker = ansattBruker, gruppe: EntraGruppe) = ansatt(bruker, setOf(gruppe))
    internal fun ansatt(bruker: Bruker = ansattBruker, grupper: Set<EntraGruppe>) = Ansatt(aids, bruker, grupper)
}