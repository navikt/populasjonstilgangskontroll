package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import com.neovisionaries.i18n.CountryCode.SE
import io.mockk.spyk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import java.util.UUID

object TestData {

    internal val enhet = Enhetsnummer("4242")
    internal val ansattId = AnsattId("Z999999")
    internal val attributter = AnsattAttributter(UUID.randomUUID(), ansattId, Navn("En", "Saksbehandler"), enhet)
    internal val vanligBrukerId = BrukerId("08526835670")
    internal val strengtFortroligBrukerId = BrukerId("08526835671")
    internal val fortroligBrukerId = BrukerId("08526835672")
    internal val ansattBrukerId = BrukerId("08526835673")
    internal val egenAnsattStrengtFortroligBrukerId = BrukerId("08526835674")
    internal val egenAnsattFortroligBrukerId = BrukerId("08526835675")
    internal val ukjentBostedBrukerId = BrukerId("08526835676")
    internal val geoUtlandBrukerId = BrukerId("08526835677")
    internal val enhetBrukerId = BrukerId("08526835678")
    internal val annenEnhetBrukerId = BrukerId("08526835679")


    internal val strengtFortroligBruker = Bruker(strengtFortroligBrukerId, UdefinertGeoTilknytning, Familie.INGEN,STRENGT_FORTROLIG_GRUPPE)
    internal val fortroligBruker = Bruker(fortroligBrukerId, UdefinertGeoTilknytning,  Familie.INGEN,FORTROLIG_GRUPPE)
    internal val vanligBruker = Bruker(vanligBrukerId, UdefinertGeoTilknytning, Familie.INGEN)
    internal val ansattBruker = Bruker(ansattBrukerId, UdefinertGeoTilknytning,  Familie.INGEN,EGEN_ANSATT_GRUPPE)
    internal val egenAnsattStrengtFortroligBruker = Bruker(egenAnsattStrengtFortroligBrukerId,
        UdefinertGeoTilknytning, Familie.INGEN,
        STRENGT_FORTROLIG_GRUPPE,
        EGEN_ANSATT_GRUPPE)
    internal val egenAnsattFortroligBruker = Bruker(egenAnsattFortroligBrukerId,
        UdefinertGeoTilknytning,
        Familie.INGEN,
        FORTROLIG_GRUPPE,
        EGEN_ANSATT_GRUPPE)
    internal val ukjentBostedBruker = Bruker(ukjentBostedBrukerId, UkjentBosted(),  Familie.INGEN,UDEFINERT_GEO_GRUPPE)
    internal val geoUtlandBruker = Bruker(geoUtlandBrukerId, UtenlandskTilknytning(SE),  Familie.INGEN,GEO_PERSON_UTLAND_GRUPPE)
    internal val enhetBruker = Bruker(enhetBrukerId, KommuneTilknytning(Kommune(enhet.verdi)), )
    internal val annenEnhetBruker = Bruker(annenEnhetBrukerId, KommuneTilknytning(Kommune("4321")), )



    internal val strengtFortroligEntraGruppe = EntraGruppe(UUID.randomUUID(), "Strengt fortrolig gruppe")
    internal val fortroligEntraGruppe = EntraGruppe(UUID.randomUUID(), "Fortrolig gruppe")
    internal val egenAnsattEntraGruppe = EntraGruppe(UUID.randomUUID(), "egen gruppe")
    internal val annenEntraGruppe = EntraGruppe(UUID.randomUUID(), "Annen gruppe")
    internal val geoUtlandEntraGruppe = EntraGruppe(UUID.randomUUID(), "Geo utland gruppe")
    internal val udefinertGruppe = EntraGruppe(UUID.randomUUID(), "Udefinert geo gruppe")
    internal val nasjonalGruppe = EntraGruppe(UUID.randomUUID(), "Nsjonal gruppe")
    internal val enhetGruppe = EntraGruppe(UUID.randomUUID(), "XXX_GEO_${enhet.verdi}")

    internal val egenAnsattFortroligAnsatt = Ansatt(attributter, fortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val egenAnsattStrengtFortroligAnsatt = Ansatt(attributter, strengtFortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val strengtFortroligAnsatt = Ansatt(attributter, strengtFortroligEntraGruppe)
    internal val fortroligAnsatt = Ansatt(attributter, fortroligEntraGruppe)
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