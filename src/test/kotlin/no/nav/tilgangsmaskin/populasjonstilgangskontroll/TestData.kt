package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.EgenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.FortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GeoNorgeRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.EGEN_ANSATT_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.GEO_PERSON_UTLAND_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.STRENGT_FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.UDEFINERT_GEO_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.StrengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.UkjentBostedGeoRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.UtlandUdefinertGeoRegel
import java.util.UUID.randomUUID

object TestData {

    internal val enhet = Enhetsnummer("4242")
    internal val navid = NavId("Z999999")
    internal val attributter = AnsattAttributter(randomUUID(),navid, Navn("En","Saksbehandler"), enhet)
    internal val fnr = Fødselsnummer("11111111111")
    internal val navn = Navn("Ola", "Nordmann")


    internal val kode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, STRENGT_FORTROLIG_GRUPPE)
    internal val kode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, FORTROLIG_GRUPPE)
    internal val vanligBruker = Bruker(fnr,navn,UdefinertGeoTilknytning)
    internal val ansattBruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE)
    internal val ansattKode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
    internal val ansattKode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, FORTROLIG_GRUPPE)
    internal val ukjentBostedBruker = Bruker(fnr, navn,UkjentBosted(), UDEFINERT_GEO_GRUPPE)
    internal val geoUtlandBruker = Bruker(fnr, navn, UtenlandskTilknytning(SE), GEO_PERSON_UTLAND_GRUPPE)
    internal val enhetBruker = Bruker(fnr, navn, KommuneTilknytning(Kommune(enhet.verdi)))
    internal val enhetBruker1 = Bruker(fnr, navn, KommuneTilknytning(Kommune("4321")))



    internal val strengtFortroligEntraGruppe = EntraGruppe(randomUUID(), "Strengt fortrolig gruppe")
    internal val fortroligEntraGruppe = EntraGruppe(randomUUID(), "Fortrolig gruppe")
    internal val egenAnsattEntraGruppe = EntraGruppe(randomUUID(), "egen gruppe")
    internal val annenEntraGruppe = EntraGruppe(randomUUID(), "Annen gruppe")
    internal val geoUtlandEntraGruppe = EntraGruppe(randomUUID(), "Geo utland gruppe")
    internal val udefinertGruppe = EntraGruppe(randomUUID(), "Udefinert geo gruppe")
    internal val nasjonalGruppe = EntraGruppe(randomUUID(), "Nsjonal gruppe")
    internal val enhetGruppe = EntraGruppe(randomUUID(), "XXX_GEO_${enhet.verdi}")

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
    internal val fortroligRegel = FortroligRegel(fortroligEntraGruppe.id)
    internal val egenAnsattRegel = EgenAnsattRegel(egenAnsattEntraGruppe.id)
    internal val ukjentBostedGeoRegel = UkjentBostedGeoRegel(udefinertGruppe.id)
    internal val geoUtlandRegel = UtlandUdefinertGeoRegel(geoUtlandEntraGruppe.id)
    internal val geoNorgeRegel = GeoNorgeRegel(nasjonalGruppe.id)

    internal val motor = RegelMotor(strengtFortroligRegel,fortroligRegel, egenAnsattRegel, ukjentBostedGeoRegel, geoUtlandRegel, geoNorgeRegel)

}