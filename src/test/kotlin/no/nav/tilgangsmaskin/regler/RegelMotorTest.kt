package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.regler.motor.EgneDataRegel
import no.nav.tilgangsmaskin.regler.motor.FellesBarnRegel
import no.nav.tilgangsmaskin.regler.motor.ForeldreOgBarnRegel
import no.nav.tilgangsmaskin.regler.motor.FortroligRegel
import no.nav.tilgangsmaskin.regler.motor.GeografiskRegel
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.PartnerRegel
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.SkjermingRegel
import no.nav.tilgangsmaskin.regler.motor.StrengtFortroligRegel
import no.nav.tilgangsmaskin.regler.motor.StrengtFortroligUtlandRegel
import no.nav.tilgangsmaskin.regler.motor.SøskenRegel
import no.nav.tilgangsmaskin.regler.motor.UkjentBostedRegel
import no.nav.tilgangsmaskin.regler.motor.UtlandRegel
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.util.*

@Import(RegelTestConfig::class)
@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureMetrics
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [RegelTestConfig::class, LocalAuditor::class])
@ApplyExtension(SpringExtension::class)
class RegelMotorTest : BehaviorSpec() {

    private val brukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")

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


        Given("bruker krever ingen spesialtilganger") {
            val bruker = BrukerBuilder(brukerId).build()
            When("ansatt er medlem av strengt fortrolig")
            Then("Tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                ansatt kanBehandle bruker
            }

            When("ansatt er medlem av fortrolig")
            Then("Tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                ansatt kanBehandle bruker
            }

            When("ansatt har ingen spesialtilganger")
            Then("Tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).build()
                ansatt kanBehandle bruker
            }
        }

        Given("bruker har strengt fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
            When("Ansatt har ingen spesialtilganger")
            Then("streng fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av fortrolig")
            Then("streng fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av streng fortrolig")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                ansatt kanBehandle bruker
            }
        }

        Given("bruker har fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
            When("ansatt er medlem av strengt fortroligs")
            Then("fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            When("ansatt har ingen spesialtilganger")
            Then("fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            When("ansatt har ingen spesialtilganger")
            And("ansatt er medlem av fortrolig")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                ansatt kanBehandle bruker
            }
        }

        Given("bruker er skjermet") {
            When("ansatt er medlem av skjerming")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                ansatt kanBehandle bruker
            }

            When("bruker i tillegg har fortrolig beskyttelse")
            And("ansatt er medlem av skjerming")
            And("ansatt i tillegg er medlem av fortrolig")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG, SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
                ansatt kanBehandle bruker
            }

            When("bruker i tillegg har strengt fortrolig beskyttelse")
            And("ansatt er medlem av skjerming")
            And("ansatt i tillegg er medlem av strengt fortrolig")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                ansatt kanBehandle bruker
            }

            When("bruker i tillegg har streng fortrolig beskyttelse")
            And("ansatt er kun medlem av skjerming")
            Then("strengt fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }

            When("bruker i tillegg har fortrolig beskyttelse")
            And("ansatt er kun medlem av skjerming")
            Then("fortrolig regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
                forventAvvistAv<FortroligRegel>(ansatt, bruker)
            }

            When("bruker i tillegg har strengt fortrolig beskyttelse")
            And("ansatt er medlem av fortrolig")
            Then("strengt fortrolig-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
                forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
            }


            When("ansatt har ingen spesialtilganger")
            Then("skjerming-regel avviser tilgang") {
            val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av skjerming")
            And("ansatt er den samme som bruker")
            Then("egne data-regel avviser tilgang") {
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()
                forventAvvistAv<EgneDataRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av fortrolig")
            Then("skjerming-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }

            And("ansatt er medlem av strengt fortrolig")
            Then("skjerming-regel avviser tilgang") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                forventAvvistAv<SkjermingRegel>(ansatt, bruker)
            }
        }

        Given("bruker har ingen gepgrafisk tilknyting") {
            When("ansatt er medlem av nasjonal")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
                val bruker = BrukerBuilder(brukerId).build()
                ansatt kanBehandle bruker
                verify { oppfølging wasNot Called }
            }
        }

        Given("bruker er bosatt i utlandet") {
            val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).build()
            When("ansatt har ingen spesialtilganger")
            Then("tilgang avvises av utland-regel") {
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<UtlandRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av utenlandsk")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UTENLANDSK).build()
                ansatt kanBehandle bruker
            }
        }
        Given("bruker har strengt fortrolig utland beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
            When("ansatt har ingen spesialtilganger")
            Then("tilgang avvises av strengt fortrolog utland-regel") {
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }

            When("ansatt er medlem av fortrolig")
            Then("tilgang avvises av strengt fortrolig utland-regel") {
                val ansatt = AnsattBuilder(ansattId)
                    .medMedlemskapI(FORTROLIG)
                    .build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }
        }

        Given("bruker har ukjent bosted") {
            val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
            When("ansatt er medlem av ukjent bosted")
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                ansatt kanBehandle bruker
            }

            When("ansatt er ikke medlem av ukjent bosted")
            Then("tilgang avvises") {
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<UkjentBostedRegel>(ansatt, bruker)
            }
        }

        Given("bruker har bydelstilknytning") {
            val bydel = "111111"
            val bruker = BrukerBuilder(brukerId, BydelTilknytning(Bydel(bydel))).build()
            When("ansatt er ikke er medlem av gruppen for denne bydelen")
            Then("tilgang avvises av geografisk-regel") {
                every { oppfølging.enhetFor(any()) } returns null
                val ansatt = AnsattBuilder(ansattId).build()
                forventAvvistAv<GeografiskRegel>(ansatt, bruker)
            }
            When("ansatt er  er medlem av gruppen for denne bydelen")
            Then("tilgang gis") {
                val bydelGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(bydelGruppe).build()
                ansatt kanBehandle bruker
            }
        }


        Given("bruker har kommunetilknytning") {
            When("ansatt er medlem av gruppe for denne kommunen")
            Then("tilgang gis og intet kall til oppfølgingstjenesten er nødvendig") {
                val enhet = Enhetsnummer("4242")
                val enhetGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_${enhet.verdi}")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
                ansatt kanBehandle bruker
                verify { oppfølging wasNot Called }
            }
        }

        Given("bruker har kommunal tilknytning og er ikke under oppfølging") {
            When("ansatt har ingen spesialtilganger")
            Then("tilgang avvises av geografisk-regel siden oppfølgingstjenesten ikke returnerer enhet") {
                every { oppfølging.enhetFor(any()) } returns null
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
            }
        }

        Given("bruker har kommunal tilknytning og er under oppfølging") {
            When("ansatt har ikke geografisk tilknytning til samme kommune")
            And("ansatt er medlem av gruppen for oppfølging")
            Then("tilgang gis") {
                val enhet = Enhetsnummer("4242")
                every { oppfølging.enhetFor(Identifikator(brukerId.verdi)) } returns enhet
                val oppfølgingGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-ENHET_${enhet.verdi}")
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(oppfølgingGruppe).build()
                val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                ansatt kanBehandle bruker
                verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
            }
        }

        Given("ansatt er nærstående til bruker") {
            When("Ansatt har felles barn med bruker")
            Then("Tilgang avvises av felle barn-regel") {
                val ansattBrukerId = BrukerId("08526835644")
                val barn = BrukerId("08526835649")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()
                forventAvvistAv<FellesBarnRegel>(ansatt, mor)
            }

            When("ansatt er partner med bruker")
            Then("Tilgang avvises av felles barn-regel") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val partner = BrukerBuilder(brukerId).build()
                forventAvvistAv<PartnerRegel>(ansatt, partner)
            }

            When("ansatt er forelder til bruker")
            Then("tilgang avvises av foreldre og barn-regel") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val barn = BrukerBuilder(brukerId).build()
                forventAvvistAv<ForeldreOgBarnRegel>(ansatt, barn)
            }

            When("ansatt er barn av til bruker")
            Then("tilgang avvises av foreldre og barn-regel") {
                val ansattBrukerId = BrukerId("08526835644")
                val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
                val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                val far = BrukerBuilder(brukerId).build()
                forventAvvistAv<ForeldreOgBarnRegel>(ansatt, far)
            }

            When("ansatt er søsken til bruker") {
                Then("tilgang avvises av søsken-regel") {
                    val ansattBrukerId = BrukerId("08526835644")
                    val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(brukerId)).build()
                    val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                    val søsken = BrukerBuilder(brukerId).build()
                    forventAvvistAv<SøskenRegel>(ansatt, søsken)
                }
            }
        }

        Given("bruker har utenlandsk eller ukjent tilknytning") {
            Then("Bruker med strengt fortrolig utland beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                ansatt kanBehandle bruker
            }

            Then("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }

            Then("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av vanlig ansatt") {
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
            }
        }
    }

    private inline fun <reified T : Regel> forventAvvistAv(ansatt: Ansatt, bruker: Bruker) {
        shouldThrow<RegelException> {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.regel.shouldBeInstanceOf<T>()
    }

    private infix fun Ansatt.kanBehandle(bruker: Bruker) {
        shouldNotThrowAny {
            regelMotor.kompletteRegler(this, bruker)
        }
    }
}

@Configuration
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
class RegelTestConfig