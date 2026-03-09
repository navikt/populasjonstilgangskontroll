package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@Import(RegelTestConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureMetrics
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class, RegelConfig::class])
@ContextConfiguration(classes = [TestApp::class, Token::class, Auditor::class])
@ApplyExtension(SpringExtension::class)
class RegelMotorTest : DescribeSpec() {

    private val brukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")

    @MockkBean
    lateinit var holder: TokenValidationContextHolder

    @MockkBean
    private lateinit var oppfølging: OppfølgingTjeneste

    @MockkBean
    private lateinit var proxy: EntraProxyTjeneste

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var regelMotor: RegelMotor

    init {

        beforeEach {
            every { token.system } returns "test"
            every { token.erObo } returns false
            every { token.erCC } returns true
            every { token.systemNavn } returns "test"
            every { token.clusterAndSystem } returns "cluster:test"
        }

        describe("SkjermingTester") {

            it("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap kun i egen ansatt gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            it("Egen ansatt bruker med fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen gruppe ansatt") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            it("Egen ansatt bruker med fortrolig beskyttelse kan behandles av ansatt med medlemsskap i egen ansatt gruppe som også har medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG, SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Egen ansatt bruker med strengt fortrolig beskyttelse kan behandles av ansatt i egen ansatt gruppe som også har strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen ansatt gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            it("Egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                regelMotor.kompletteRegler(ansatt, bruker)
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Egen ansatt bruker kan ikke behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }

            it("Ansatt kan ikke behandle seg selv") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()
                forventAvvistAv<EgneDataRegel>(ansatt, bruker)
            }

            it("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }

            it("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }
        }

        describe("FortroligTester") {

            it("Fortrolig bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            it("Bruker med fortrolig beskyttelse kan ikke behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            it("Fortrolig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }
        }

        describe("GeoTester") {
            val enhet = Enhetsnummer("4242")
            val enhetGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_${enhet.verdi}")
            val oppfølgingGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")

            it("Ansatt med nasjonal tilgang kan behandle vanlig bruker") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
                verify { oppfølging wasNot Called }
            }

            it("Ansatt med manglende geografisk tilknytning gruppe kan behandle bruker uten kjent geografisk tilknytning") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Ansatt med tilgang som samme GT som bruker kan behandle denne") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
                verify { oppfølging wasNot Called }
            }

            it("Ansatt uten tilgang som samme GT som bruker og uten tilgang til oppfølgingsenhet kan ikke behandle denne") {
                every { oppfølging.enhetFor(any()) } returns null
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                verify(exactly = 1) { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
            }

            it("Ansatt uten Nasjonal tilgang og uten GT kan likevel behandle om den har tilgang til brukerens oppfølgingsenhet") {
                every { oppfølging.enhetFor(Identifikator(brukerId.verdi)) } returns enhet
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(oppfølgingGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
                verify(exactly = 1) { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
            }
        }

        describe("NærståendeTester") {
            val annenAnsattBrukerId = BrukerId("08526835644")

            it("Ansatt kan ikke behandle noen de har felles barn med") {
                val barn = BrukerId("08526835649")
                val far = BrukerBuilder(annenAnsattBrukerId).barn(setOf(barn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(far).build()
                val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()
                forventAvvistAv<FellesBarnRegel>(ansatt, mor)
            }

            it("Ansatt kan ikke behandle egen partner") {
                val ansattBruker = BrukerBuilder(annenAnsattBrukerId).partnere(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val partner = BrukerBuilder(brukerId).build()
                forventAvvistAv<PartnerRegel>(ansatt, partner)
            }

            it("Ansatt kan ikke behandle egne barn") {
                val ansattBruker = BrukerBuilder(annenAnsattBrukerId).barn(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val barn = BrukerBuilder(brukerId).build()
                forventAvvistAv<ForeldreOgBarnRegel>(ansatt, barn)
            }

            it("Ansatt kan ikke behandle egne foreldre") {
                val ansattBruker = BrukerBuilder(annenAnsattBrukerId).far(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val far = BrukerBuilder(brukerId).build()
                forventAvvistAv<ForeldreOgBarnRegel>(ansatt, far)
            }

            it("Ansatt kan ikke behandle egne søsken") {
                val ansattBruker = BrukerBuilder(annenAnsattBrukerId).søsken(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val søsken = BrukerBuilder(brukerId).build()
                forventAvvistAv<SøskenRegel>(ansatt, søsken)
            }
        }

        describe("StrengtFortroligTester") {

            it("Bruker med strengt fortrolig beskyttelse kan ikke behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            it("Bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            it("Bruker med strengt fortrolig beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }
        }

        describe("UtlandTester") {

            it("Bruker med strengt fortrolig utland beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }

            it("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }
        }

        describe("VanligBrukerTest") {

            it("Vanlig bruker kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Vanlig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }

            it("Vanlig bruker kan behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                (ansatt kanBehandle bruker).shouldBeTrue()
            }
        }
    }

    private inline fun <reified T : Regel> forventAvvistAv(ansatt: Ansatt, bruker: Bruker) {
        shouldThrow<RegelException> {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.regel.shouldBeInstanceOf<T>()
    }

    private infix fun Ansatt.kanBehandle(bruker: Bruker): Boolean {
        shouldNotThrowAny {
            regelMotor.kompletteRegler(this, bruker)
        }
        return true
    }
}