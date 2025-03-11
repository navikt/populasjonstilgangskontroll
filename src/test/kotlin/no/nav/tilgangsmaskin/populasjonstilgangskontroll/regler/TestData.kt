package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import com.neovisionaries.i18n.CountryCode.SE
import io.mockk.spyk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import java.util.UUID

object TestData {

    internal val enhet = Enhetsnummer("4242")
    internal val ansattId = AnsattId("Z999999")
    internal val attributter = AnsattAttributter(UUID.randomUUID(), ansattId, enhet)
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


    internal val strengtFortroligBruker = Bruker(strengtFortroligBrukerId, UdefinertGeoTilknytning, INGEN,STRENGT_FORTROLIG_GRUPPE)
    internal val fortroligBruker = Bruker(fortroligBrukerId, UdefinertGeoTilknytning, INGEN,FORTROLIG_GRUPPE)
    internal val vanligBruker = Bruker(vanligBrukerId, UdefinertGeoTilknytning, INGEN)
    internal val annenAnsattBruker = Bruker(annenAnsattBrukerId, UdefinertGeoTilknytning,  Familie(barn = listOf(Familie.FamilieMedlem(vanligBrukerId, FamilieRelasjon.BARN))),EGEN_ANSATT_GRUPPE)
    internal val ansattBruker = Bruker(ansattBrukerId, UdefinertGeoTilknytning,  INGEN,EGEN_ANSATT_GRUPPE)
    internal val egenAnsattStrengtFortroligBruker = Bruker(egenAnsattStrengtFortroligBrukerId, UdefinertGeoTilknytning, INGEN,STRENGT_FORTROLIG_GRUPPE, EGEN_ANSATT_GRUPPE)
    internal val egenAnsattFortroligBruker = Bruker(egenAnsattFortroligBrukerId, UdefinertGeoTilknytning, INGEN, FORTROLIG_GRUPPE, EGEN_ANSATT_GRUPPE)
    internal val ukjentBostedBruker = Bruker(ukjentBostedBrukerId, UkjentBosted(), INGEN,UDEFINERT_GEO_GRUPPE)
    internal val geoUtlandBruker = Bruker(geoUtlandBrukerId, UtenlandskTilknytning(SE), INGEN,GEO_PERSON_UTLAND_GRUPPE)
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

    internal val egenAnsattFortroligAnsatt = Ansatt(ansattBruker,ansattId,attributter, fortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val egenAnsattStrengtFortroligAnsatt = Ansatt(ansattBruker,ansattId,attributter, strengtFortroligEntraGruppe, egenAnsattEntraGruppe)
    internal val strengtFortroligAnsatt = Ansatt(ansattBruker,ansattId,attributter, strengtFortroligEntraGruppe)
    internal val fortroligAnsatt = Ansatt(ansattBruker,ansattId,attributter, fortroligEntraGruppe)
    internal val egenAnsatt = Ansatt(ansattBruker,ansattId,attributter, egenAnsattEntraGruppe)
    internal val annenEgenAnsatt = Ansatt(annenAnsattBruker,ansattId,attributter, egenAnsattEntraGruppe)
    internal val egenAnsattMedFamilie = Ansatt(annenAnsattBruker,ansattId,attributter, annenEntraGruppe)

    internal val vanligAnsatt = Ansatt(ansattBruker,ansattId,attributter, annenEntraGruppe)
    internal val geoUtlandAnsatt = Ansatt(ansattBruker,ansattId,attributter, geoUtlandEntraGruppe)
    internal val udefinertGeoAnsatt = Ansatt(ansattBruker,ansattId,attributter, udefinertGruppe)
    internal val nasjonalAnsatt = Ansatt(ansattBruker,ansattId,attributter, nasjonalGruppe)
    internal val enhetAnsatt = Ansatt(ansattBruker,ansattId,attributter, enhetGruppe)

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

    internal val egneDataRegel = EgneDataRegel()
    internal val spyEgneDataRegel= spyk(egneDataRegel)

    internal val egenFamilieRegel = EgenFamilieRegel()
    internal val spyEgenFamilieRegel= spyk(egenFamilieRegel)

    private val allSpies = listOf(spyStrengtFortroligRegel, spyFortroligRegel, spyEgenAnsattRegel,spyEgneDataRegel, egenFamilieRegel, spyUkjentBostedGeoRegel, spyGeoUtlandRegel, spyGeoNorgeRegel).toTypedArray()

    internal val spiesMotor = RegelMotor(*allSpies)
    internal val motor = RegelMotor(strengtFortroligRegel,fortroligRegel, egenAnsattRegel,egneDataRegel,egenFamilieRegel,ukjentBostedGeoRegel, geoUtlandRegel, geoNorgeRegel)

}