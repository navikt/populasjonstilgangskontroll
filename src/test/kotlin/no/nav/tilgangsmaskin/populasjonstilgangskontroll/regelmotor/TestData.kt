package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt.AnsattIdentifikatorer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker.BrukerIdentifikatorer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem.FamilieRelasjon
import java.util.UUID

object TestData {

    internal val aktørId = AktørId("1234567890123")
    internal val enhet = Enhetsnummer("4242")
    internal val ansattId = AnsattId("Z999999")
    internal val vanligBrukerId = BrukerId("08526835670")
    internal val strengtFortroligBrukerId = BrukerId("08526835671")
    internal val fortroligBrukerId = BrukerId("08526835672")
    internal val annenAnsattBrukerId = BrukerId("08526835644")
    internal val ansattBrukerId = BrukerId("08526835673")
    internal val egenAnsattStrengtFortroligBrukerId = BrukerId("08526835674")
    internal val egenAnsattFortroligBrukerId = BrukerId("08526835675")
    internal val ukjentBostedBrukerId = BrukerId("08526835676")
    internal val geoUtlandBrukerId = BrukerId("08526835677")
    internal val enhetBrukerId = BrukerId("08526835678")
    internal val annenEnhetBrukerId = BrukerId("08526835679")
    internal val historiskBrukerId = BrukerId("11111111111")

    internal val oid = UUID.randomUUID()
    internal val strengtFortroligBruker = Bruker(BrukerIdentifikatorer(strengtFortroligBrukerId, aktørId), UdefinertGeoTilknytning, listOf(STRENGT_FORTROLIG_GRUPPE))
    internal val fortroligBruker = Bruker(BrukerIdentifikatorer(fortroligBrukerId, aktørId), UdefinertGeoTilknytning, listOf(FORTROLIG_GRUPPE))
    internal val vanligBruker = Bruker(BrukerIdentifikatorer(vanligBrukerId, aktørId), UdefinertGeoTilknytning)
    internal val vanligHistoriskBruker = Bruker(BrukerIdentifikatorer(historiskBrukerId, aktørId), UdefinertGeoTilknytning)
    internal val vanligBrukerMedHistoriskIdent = Bruker(BrukerIdentifikatorer(vanligBrukerId, aktørId,listOf(vanligHistoriskBruker.brukerId)), UdefinertGeoTilknytning)
    internal val annenAnsattBruker = Bruker(BrukerIdentifikatorer(annenAnsattBrukerId, aktørId), UdefinertGeoTilknytning, listOf(EGEN_ANSATT_GRUPPE), Familie(barn = setOf(FamilieMedlem(vanligBrukerId, FamilieRelasjon.BARN))))
    internal val ansattBruker = Bruker(BrukerIdentifikatorer(ansattBrukerId, aktørId), UdefinertGeoTilknytning, listOf(EGEN_ANSATT_GRUPPE),)
    internal val egenAnsattStrengtFortroligBruker = Bruker(BrukerIdentifikatorer(egenAnsattStrengtFortroligBrukerId, aktørId), UdefinertGeoTilknytning, listOf(STRENGT_FORTROLIG_GRUPPE, EGEN_ANSATT_GRUPPE),)
    internal val egenAnsattFortroligBruker = Bruker(BrukerIdentifikatorer(egenAnsattFortroligBrukerId, aktørId), UdefinertGeoTilknytning, listOf(FORTROLIG_GRUPPE, EGEN_ANSATT_GRUPPE),)
    internal val ukjentBostedBruker = Bruker(BrukerIdentifikatorer(ukjentBostedBrukerId, aktørId), UkjentBosted(), listOf(UDEFINERT_GEO_GRUPPE),)
    internal val geoUtlandBruker = Bruker(BrukerIdentifikatorer(geoUtlandBrukerId, aktørId), UtenlandskTilknytning(), listOf(GEO_PERSON_UTLAND_GRUPPE),)
    internal val enhetBruker = Bruker(BrukerIdentifikatorer(enhetBrukerId, aktørId), KommuneTilknytning(Kommune(enhet.verdi)))
    internal val annenEnhetBruker = Bruker(BrukerIdentifikatorer(annenEnhetBrukerId, aktørId), KommuneTilknytning(Kommune("4321")), )

    internal val strengtFortroligEntraGruppe = EntraGruppe(UUID.fromString("5ef775f2-61f8-4283-bf3d-8d03f428aa14"), "Strengt fortrolig gruppe")
    internal val fortroligEntraGruppe = EntraGruppe(UUID.fromString("ea930b6b-9397-44d9-b9e6-f4cf527a632a"), "Fortrolig gruppe")
    internal val egenAnsattEntraGruppe = EntraGruppe(UUID.fromString("dbe4ad45-320b-4e9a-aaa1-73cca4ee124d"), "egen gruppe")
    internal val annenEntraGruppe = EntraGruppe(UUID.randomUUID(), "Annen gruppe")
    internal val geoUtlandEntraGruppe = EntraGruppe(UUID.fromString("de62a4bf-957b-4cde-acdb-6d8bcbf821a0"), "Geo utland gruppe")
    internal val udefinertGruppe = EntraGruppe(UUID.fromString("35d9d1ac-7fcb-4a22-9155-e0d1e57898a8"), "Udefinert geo gruppe")
    internal val nasjonalGruppe = EntraGruppe(UUID.fromString("c7107487-310d-4c06-83e0-cf5395dc3be3"), "Nsjonal gruppe")
    internal val enhetGruppe = EntraGruppe(UUID.randomUUID(), "XXX_GEO_${enhet.verdi}")

    internal val egenAnsattFortroligAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(fortroligEntraGruppe, egenAnsattEntraGruppe), ansattBruker)
    internal val egenAnsattStrengtFortroligAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(strengtFortroligEntraGruppe, egenAnsattEntraGruppe), ansattBruker)
    internal val strengtFortroligAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(strengtFortroligEntraGruppe), ansattBruker)
    internal val fortroligAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(fortroligEntraGruppe), ansattBruker)
    internal val egenAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(egenAnsattEntraGruppe), ansattBruker)
    internal val egenAnsattMedFamilie = Ansatt(AnsattIdentifikatorer(ansattId, oid, annenAnsattBruker.brukerId), listOf(annenEntraGruppe), annenAnsattBruker)

    internal val vanligAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(annenEntraGruppe), ansattBruker)
    internal val geoUtlandAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(geoUtlandEntraGruppe), ansattBruker)
    internal val udefinertGeoAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(udefinertGruppe), ansattBruker)
    internal val nasjonalAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(nasjonalGruppe), ansattBruker)
    internal val enhetAnsatt = Ansatt(AnsattIdentifikatorer(ansattId, oid, ansattBruker.brukerId), listOf(enhetGruppe), ansattBruker)

}