package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import com.neovisionaries.i18n.CountryCode.SE
import io.mockk.spyk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import java.util.UUID

object TestData {

    internal val enhet = Enhetsnummer("4242")
    internal val ansattId = AnsattId("Z999999")
    internal val attributter = AnsattAttributter(UUID.randomUUID(), ansattId, Navn("En", "Saksbehandler"), enhet)
    internal val brukerId = BrukerId("08526835671")
    internal val navn = Navn("Ola", "Nordmann")

    internal val kode6Bruker = Bruker(brukerId, navn, UdefinertGeoTilknytning, STRENGT_FORTROLIG_GRUPPE)
    internal val kode7Bruker = Bruker(brukerId, navn, UdefinertGeoTilknytning, FORTROLIG_GRUPPE)
    internal val vanligBruker = Bruker(brukerId, navn, UdefinertGeoTilknytning)
    internal val ansattBruker = Bruker(brukerId, navn, UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE)
    internal val ansattKode6Bruker = Bruker(brukerId, navn, UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
    internal val ansattKode7Bruker = Bruker(brukerId, navn, UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, FORTROLIG_GRUPPE)
    internal val ukjentBostedBruker = Bruker(brukerId, navn, UkjentBosted(), UDEFINERT_GEO_GRUPPE)
    internal val geoUtlandBruker = Bruker(brukerId, navn, UtenlandskTilknytning(SE), GEO_PERSON_UTLAND_GRUPPE)
    internal val enhetBruker = Bruker(brukerId, navn, KommuneTilknytning(Kommune(enhet.verdi)))
    internal val enhetBruker1 = Bruker(brukerId, navn, KommuneTilknytning(Kommune("4321")))



    internal val strengtFortroligEntraGruppe = EntraGruppe(UUID.randomUUID(), "Strengt fortrolig gruppe")
    internal val fortroligEntraGruppe = EntraGruppe(UUID.randomUUID(), "Fortrolig gruppe")
    internal val egenAnsattEntraGruppe = EntraGruppe(UUID.randomUUID(), "egen gruppe")
    internal val annenEntraGruppe = EntraGruppe(UUID.randomUUID(), "Annen gruppe")
    internal val geoUtlandEntraGruppe = EntraGruppe(UUID.randomUUID(), "Geo utland gruppe")
    internal val udefinertGruppe = EntraGruppe(UUID.randomUUID(), "Udefinert geo gruppe")
    internal val nasjonalGruppe = EntraGruppe(UUID.randomUUID(), "Nsjonal gruppe")
    internal val enhetGruppe = EntraGruppe(UUID.randomUUID(), "XXX_GEO_${enhet.verdi}")

    internal val kode7EgenAnsatt = Ansatt(attributter, fortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val kode6EgenAnsatt = Ansatt(attributter, strengtFortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val kode6Ansatt = Ansatt(attributter, strengtFortroligEntraGruppe)
    internal val kode7Ansatt = Ansatt(attributter, fortroligEntraGruppe)
    internal val egenAnsatt = Ansatt(attributter, egenAnsattEntraGruppe)
    internal val vanligAnsatt = Ansatt(attributter, annenEntraGruppe)
    internal val geoUtlandAnsatt = Ansatt(attributter, geoUtlandEntraGruppe)
    internal val udefinertGeoAnsatt = Ansatt(attributter, udefinertGruppe)
    internal val nasjonalAnsatt = Ansatt(attributter, nasjonalGruppe)
    internal val enhetAnsatt = Ansatt(attributter, enhetGruppe)

    internal val strengtFortroligRegel = StrengtFortroligRegel(strengtFortroligEntraGruppe.id)
    internal val spyStrengtFortroligRegel = spyk(strengtFortroligRegel)

    internal val fortroligRegel = FortroligRegel(fortroligEntraGruppe.id)
    internal val spyFortroligRegel = spyk(fortroligRegel)

    internal val egenAnsattRegel = EgenAnsattRegel(egenAnsattEntraGruppe.id)
    internal val spyEgenAnsattRegel= spyk(egenAnsattRegel)

    internal val ukjentBostedGeoRegel = UkjentBostedGeoRegel(udefinertGruppe.id)
    internal val spyUkjentBostedGeoRegel= spyk(ukjentBostedGeoRegel)

    internal val geoUtlandRegel = UtlandUdefinertGeoRegel(geoUtlandEntraGruppe.id)
    internal val spyGeoUtlandRegel= spyk(geoUtlandRegel)

    internal val geoNorgeRegel = GeoNorgeRegel(nasjonalGruppe.id)
    internal val spyGeoNorgeRegel= spyk(geoNorgeRegel)

    private val allSpies = listOf(spyStrengtFortroligRegel, spyFortroligRegel, spyEgenAnsattRegel, spyUkjentBostedGeoRegel, spyGeoUtlandRegel, spyGeoNorgeRegel).toTypedArray()

    internal val spiesMotor = RegelMotor(*allSpies)
    internal val motor = RegelMotor(strengtFortroligRegel,fortroligRegel, egenAnsattRegel, ukjentBostedGeoRegel, geoUtlandRegel, geoNorgeRegel)

}