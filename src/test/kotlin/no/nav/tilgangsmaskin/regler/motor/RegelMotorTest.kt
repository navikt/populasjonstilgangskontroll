package no.nav.tilgangsmaskin.regler.motor

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.AVDØD
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
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
import no.nav.tilgangsmaskin.felles.LocalAuditor
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.TokenType
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate.now
import java.util.UUID

@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureMetrics
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [LocalAuditor::class])
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
@ApplyExtension(SpringExtension::class)
class RegelMotorTest : BehaviorSpec() {

    private val brukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")

    @MockkBean
    private lateinit var oppfølging: OppfølgingTjeneste

    @MockkBean
    private lateinit var proxy: EntraProxyTjeneste

    @MockkBean
    private lateinit var vergemål: VergemålTjeneste

    @MockkBean
    private lateinit var nom: NomTjeneste

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var regelMotor: RegelMotor


    init {

        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns brukerId
            every { vergemål.vergemål(any()) } returns emptySet()
            every { token.system } returns "test"
            every { token.system } returns "test"
            every { token.type } returns CCF
            every { token.systemNavn } returns "test"
            every { token.clusterAndSystem } returns "cluster:test"
        }


        Given("bruker krever ingen spesialtilganger") {
            val bruker = BrukerBuilder(brukerId).build()
            When("ansatt er medlem av strengt fortrolig") {
                Then("Tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("Tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("Tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker har strengt fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
            When("Ansatt har ingen spesialtilganger") {
                Then("streng fortrolig-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("streng fortrolig-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av streng fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker har fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
            When("ansatt er medlem av strengt fortrolig") {
                Then("fortrolig-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("fortrolig-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker er skjermet") {
            When("ansatt er medlem av skjerming") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                    ansatt kanBehandle bruker
                }
            }

            When("bruker i tillegg har fortrolig beskyttelse") {
                And("ansatt er medlem av skjerming og fortrolig") {
                    Then("tilgang gis") {
                        val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG,
                            SKJERMING).build()
                        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG,
                            SKJERMING).build()
                        ansatt kanBehandle bruker
                    }
                }
            }

            When("bruker i tillegg har strengt fortrolig beskyttelse") {
                And("ansatt er medlem av skjerming og strengt fortrolig") {
                    Then("tilgang gis") {
                        val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG,
                            SKJERMING).build()
                        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG,
                            SKJERMING).build()
                        ansatt kanBehandle bruker
                    }
                }

                And("ansatt er kun medlem av skjerming") {
                    Then("strengt fortrolig-regel avviser tilgang") {
                        val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG,
                            SKJERMING).build()
                        forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                    }
                }
            }

            When("bruker i tillegg har fortrolig beskyttelse og ansatt er kun medlem av skjerming") {
                Then("fortrolig regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG,
                        SKJERMING).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }

            When("bruker i tillegg har strengt fortrolig beskyttelse og ansatt er medlem av fortrolig") {
                Then("strengt fortrolig-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG,
                        SKJERMING).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("skjerming-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av skjerming og er den samme som bruker") {
                Then("egne data-regel avviser tilgang") {
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()
                    forventAvvistAv<EgneDataRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("skjerming-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av strengt fortrolig") {
                Then("skjerming-regel avviser tilgang") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }
        }

        Given("bruker har ingen geografisk tilknytning") {
            When("ansatt er medlem av nasjonal") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(EntraGlobalGruppe.NASJONAL).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                    verify { oppfølging wasNot Called }
                }
            }
        }

        Given("bruker er bosatt i utlandet") {
            val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).build()
            When("ansatt har ingen spesialtilganger") {
                Then("tilgang avvises av utland-regel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<UtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av utenlandsk") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UTENLANDSK).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker har strengt fortrolig utland beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
            When("ansatt har ingen spesialtilganger") {
                Then("tilgang avvises av strengt fortrolig utland-regel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("tilgang avvises av strengt fortrolig utland-regel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }
        }

        Given("bruker har ukjent bosted") {
            val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
            When("ansatt er medlem av ukjent bosted") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt er ikke medlem av ukjent bosted") {
                Then("tilgang avvises") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<UkjentBostedRegel>(ansatt, bruker)
                }
            }
        }

        Given("bruker har bydelstilknytning") {
            val bydel = "111111"
            val bruker = BrukerBuilder(brukerId, BydelTilknytning(Bydel(bydel))).build()
            When("ansatt er ikke er medlem av gruppen for denne bydelen") {
                Then("tilgang avvises av geografisk-regel") {
                    every { oppfølging.enhetFor(any()) } returns null
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                }
            }
            When("ansatt er medlem av gruppen for denne bydelen") {
                Then("tilgang gis") {
                    val bydelGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_$bydel")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(bydelGruppe).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bruker har kommunetilknytning") {
            When("ansatt er medlem av gruppe for denne kommunen") {
                Then("tilgang gis og intet kall til oppfølgingstjenesten er nødvendig") {
                    val enhet = Enhetsnummer("4242")
                    val enhetGruppe = EntraGruppe(UUID.randomUUID(), "0000-GA-GEO_${enhet.verdi}")
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
                    ansatt kanBehandle bruker
                    verify { oppfølging wasNot Called }
                }
            }
        }

        Given("bruker har kommunal tilknytning og er ikke under oppfølging") {
            When("ansatt har ingen spesialtilganger") {
                Then("tilgang avvises av geografisk-regel siden oppfølgingstjenesten ikke returnerer enhet") {
                    every { oppfølging.enhetFor(any()) } returns null
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
                    forventAvvistAv<GeografiskRegel>(ansatt, bruker)
                    verify { oppfølging.enhetFor(Identifikator(brukerId.verdi)) }
                }
            }
        }

        Given("bruker har kommunal tilknytning og er under oppfølging") {
            When("ansatt har ikke geografisk tilknytning til samme kommune og er medlem av gruppen for oppfølging") {
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
        }

        Given("ansatt er nærstående til bruker") {
            When("ansatt har felles barn med bruker") {
                Then("tilgang avvises av felles barn-regel") {
                    val ansattBrukerId = BrukerId("08526835644")
                    val barn = BrukerId("08526835649")
                    val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
                    val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                    val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()
                    forventAvvistAv<FellesBarnRegel>(ansatt, mor)
                }
            }

            When("ansatt er partner med bruker") {
                Then("tilgang avvises av partner-regel") {
                    val ansattBrukerId = BrukerId("08526835644")
                    val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
                    val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                    val partner = BrukerBuilder(brukerId).build()
                    forventAvvistAv<PartnerRegel>(ansatt, partner)
                }
            }

            When("ansatt er forelder til bruker") {
                Then("tilgang avvises av foreldre og barn-regel") {
                    val ansattBrukerId = BrukerId("08526835644")
                    val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
                    val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                    val barn = BrukerBuilder(brukerId).build()
                    forventAvvistAv<ForeldreOgBarnRegel>(ansatt, barn)
                }
            }

            When("ansatt er barn av bruker") {
                Then("tilgang avvises av foreldre og barn-regel") {
                    val ansattBrukerId = BrukerId("08526835644")
                    val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
                    val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
                    val far = BrukerBuilder(brukerId).build()
                    forventAvvistAv<ForeldreOgBarnRegel>(ansatt, far)
                }
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
            When("ansatt er medlem av strengt fortrolig") {
                Then("bruker med strengt fortrolig utland beskyttelse kan behandles") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt er medlem av fortrolig") {
                Then("bruker med strengt fortrolig utland beskyttelse kan ikke behandles") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er vanlig ansatt uten spesialtilganger") {
                Then("bruker med strengt fortrolig utland beskyttelse kan ikke behandles") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }
        }

        Given("bruker er avdød") {
            When("bruker er død for mindre enn 12 måneder siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(6)).build()

                Then("tilgang gis uavhengig av gruppetilhørighet") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    ansatt kanBehandle bruker
                }
            }

            When("bruker er død for mer enn 12 måneder siden og ansatt mangler AVDØD-gruppe") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()

                Then("tilgang avvises av AvdødBrukerDevRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<AvdødBrukerRegel>(ansatt, bruker)
                }
            }

            When("bruker er død for mer enn 12 måneder siden og ansatt har AVDØD-gruppe") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()

                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(AVDØD).build()
                    ansatt kanBehandle bruker
                }
            }

            When("bruker er død for mer enn 24 måneder siden og ansatt mangler AVDØD-gruppe") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()

                Then("tilgang avvises av AvdødBrukerDevRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<AvdødBrukerRegel>(ansatt, bruker)
                }
            }
        }

        Given("ansatt har vergemål for bruker") {
            When("vergemålstjenesten returnerer bruker") {
                Then("tilgang avvises av VergemålDevRegel") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    forventAvvistAv<VergemålRegel>(ansatt, bruker)
                }
            }
        }

        Given("ansatt har ikke vergemål for bruker") {
            When("vergemålstjenesten returnerer tom liste") {
                Then("tilgang gis") {
                    every { vergemål.vergemål(ansattId) } returns emptySet()
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("vergemålstjenesten feiler") {
            When("oppslaget kaster exception") {
                Then("tilgang gis (feilen svelges)") {
                    every { vergemål.vergemål(ansattId) } throws RuntimeException("tjenesten er nede")
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("bulkRegler") {
            When("alle brukere passerer reglene") {
                Then("alle returneres som ok") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker1 = BrukerBuilder(brukerId).build()
                    val bruker2 = BrukerBuilder(BrukerId("08526835644")).build()
                    val resultater = regelMotor.bulkRegler(ansatt, setOf(
                        BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE),
                        BrukerOgRegelsett(bruker2, KOMPLETT_REGELTYPE)
                    ))
                    assertSoftly(resultater) {
                        shouldHaveSize(2)
                        all { it.status == NO_CONTENT } shouldBe true
                    }
                }
            }

            When("en bruker avvises av regel") {
                Then("avvist bruker får FORBIDDEN, godkjent bruker får NO_CONTENT") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val godkjentBruker = BrukerBuilder(brukerId).build()
                    val avvistBruker = BrukerBuilder(BrukerId("08526835644"))
                        .kreverMedlemskapI(STRENGT_FORTROLIG).build()
                    val resultater = regelMotor.bulkRegler(ansatt, setOf(
                        BrukerOgRegelsett(godkjentBruker, KOMPLETT_REGELTYPE),
                        BrukerOgRegelsett(avvistBruker, KOMPLETT_REGELTYPE)
                    ))
                    assertSoftly(resultater) {
                        shouldHaveSize(2)
                        single { it.bruker == godkjentBruker }.status shouldBe NO_CONTENT
                        single { it.bruker == avvistBruker }.status shouldBe FORBIDDEN
                        single { it.bruker == avvistBruker }.regel.shouldBeInstanceOf<StrengtFortroligRegel>()
                    }
                }
            }

            When("enkelt bruker evalueres") {
                Then("evalueringstype er ENKELT") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    val resultater = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(bruker, KOMPLETT_REGELTYPE)))
                    assertSoftly(resultater) {
                        shouldHaveSize(1)
                        single().status shouldBe NO_CONTENT
                    }
                }
            }

            When("tomt sett med brukere") {
                Then("returneres tomt sett") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    regelMotor.bulkRegler(ansatt, emptySet()).shouldBeEmpty()
                }
            }

            When("bruker evalueres med KJERNE_REGELTYPE") {
                Then("kun kjerneregler evalueres") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val bruker = BrukerBuilder(brukerId).build()
                    val resultater = regelMotor.bulkRegler(ansatt, setOf(BrukerOgRegelsett(bruker, KJERNE_REGELTYPE)))
                    assertSoftly(resultater) {
                        shouldHaveSize(1)
                        single().status shouldBe NO_CONTENT
                    }
                }
            }

            When("brukere evalueres med forskjellige regeltyper") {
                Then("hver bruker evalueres mot sitt eget regelsett") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val vanligBruker = BrukerBuilder(brukerId).build()
                    val utlandsBruker = BrukerBuilder(BrukerId("08526835644"))
                        .gt(UtenlandskTilknytning()).build()

                    val resultater = regelMotor.bulkRegler(ansatt, setOf(
                        BrukerOgRegelsett(vanligBruker, KOMPLETT_REGELTYPE),
                        BrukerOgRegelsett(utlandsBruker, KJERNE_REGELTYPE)
                    ))

                    assertSoftly(resultater) {
                        shouldHaveSize(2)
                        single { it.bruker == vanligBruker }.status shouldBe NO_CONTENT
                        single { it.bruker == utlandsBruker }.status shouldBe NO_CONTENT
                    }
                }
            }

            When("samme bruker hadde blitt avvist med komplett regelsett") {
                Then("utlandsBruker avvises av UtlandRegel med KOMPLETT_REGELTYPE") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    val utlandsBruker = BrukerBuilder(BrukerId("08526835644"))
                        .gt(UtenlandskTilknytning()).build()

                    val resultater = regelMotor.bulkRegler(ansatt, setOf(
                        BrukerOgRegelsett(utlandsBruker, KOMPLETT_REGELTYPE)
                    ))

                    assertSoftly(resultater) {
                        shouldHaveSize(1)
                        single().status shouldBe FORBIDDEN
                        single().regel.shouldBeInstanceOf<UtlandRegel>()
                    }
                }
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