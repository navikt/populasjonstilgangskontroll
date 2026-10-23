package no.nav.tilgangsmaskin.regler

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import no.nav.sikkerhetstjenesten.felles.notifikasjon.LocalAuditor
import no.nav.sikkerhetstjenesten.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.KjerneRegel
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import java.net.URI

class RegelTjenesteTest : BehaviorSpec() {
    private val motor = mockk<RegelMotor>()
    private val brukere = mockk<BrukerTjeneste>()
    private val ansatte = mockk<AnsattTjeneste>()
    private val enkeltTilgang = mockk<EnkeltTilgangTjeneste>()

    init {
        val vanligBrukerId = BrukerId("08526835670")
        val ansattId = AnsattId("Z999999")

        lateinit var regler: RegelTjeneste

        beforeEach {
            clearAllMocks()
            every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
            regler =
                RegelTjeneste(
                    motor,
                    brukere,
                    ansatte,
                    enkeltTilgang,
                    LocalAuditor(),
                    BulkResponsAggregator(enkeltTilgang, LocalAuditor(), motor),
                )
        }

        Given("bulk-tilgangskontroll") {
            When("avvist bruker og enkelttilgang er registrert") {
                Then("havner bruker i godkjente") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                    every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(funnetBruker)
                    every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.avvist(funnetBruker, regelException))
                    every { enkeltTilgang.tilganger(any(), any()) } returns setOf(vanligBrukerId)
                    every { motor.kjerneregler(any(), funnetBruker) } returns Unit

                    val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))
                    assertSoftly(resultater) {
                        godkjente.single().brukerId shouldBe vanligBrukerId.verdi
                        avviste.shouldBeEmpty()
                        ukjente.shouldBeEmpty()
                    }
                }
            }
            When("avvist bruker og ingen enkelttilgang er registrert") {
                Then("havner bruker i avviste") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                    every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(funnetBruker)
                    every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.avvist(funnetBruker, regelException))
                    every { enkeltTilgang.tilganger(any(), any()) } returns emptySet()

                    val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))
                    assertSoftly(resultater) {
                        avviste.single().brukerId shouldBe vanligBrukerId.verdi
                        godkjente.shouldBeEmpty()
                        ukjente.shouldBeEmpty()
                    }
                }
            }
            When("id ikke finnes i PDL") {
                Then("gis tilgang") {
                    val ikkeFunnetId = BrukerId("11111111111")
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi, ikkeFunnetId.verdi)) } returns setOf(funnetBruker)
                    every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.ok(funnetBruker))
                    every { enkeltTilgang.tilganger(any(), any()) } returns emptySet()

                    val resultater =
                        regler.bulkRegler(
                            ansattId,
                            setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi), BrukerIdOgRegelsett(ikkeFunnetId.verdi)),
                        )
                    assertSoftly(resultater) {
                        godkjente.map { it.brukerId } shouldContainExactlyInAnyOrder listOf(vanligBrukerId.verdi, ikkeFunnetId.verdi)
                        avviste.shouldBeEmpty()
                        ukjente.shouldBeEmpty()
                    }
                }
            }
            When("oppslag med historisk id som matcher funnet bruker") {
                Then("regnes ikke som ukjent") {
                    val historiskId = BrukerId("22222222222")
                    val funnetBruker = BrukerBuilder(vanligBrukerId).historiske(setOf(historiskId)).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi, historiskId.verdi)) } returns setOf(funnetBruker)
                    every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.ok(funnetBruker))
                    every { enkeltTilgang.tilganger(any(), any()) } returns emptySet()

                    val resultater =
                        regler.bulkRegler(
                            ansattId,
                            setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi), BrukerIdOgRegelsett(historiskId.verdi)),
                        )
                    assertSoftly(resultater) {
                        godkjente.map { it.brukerId } shouldContainExactlyInAnyOrder listOf(vanligBrukerId.verdi)
                        avviste.shouldBeEmpty()
                        ukjente.shouldBeEmpty()
                    }
                }
            }
            When("exception ikke er RegelException") {
                Then("kastes exception videre") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(funnetBruker)
                    every { motor.bulkRegler(any(), any()) } throws RuntimeException("noe gikk galt")
                    shouldThrow<RuntimeException> {
                        regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))
                    }
                }
            }
        }

        Given("kjerneregler") {
            When("bruker ikke finnes i PDL") {
                Then("gis tilgang") {
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } throws NotFoundRestException(URI.create("http://pdl"))
                    shouldNotThrowAny {
                        regler.kjerneregler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
            When("PDL-oppslag kaster annen exception") {
                Then("kastes exception videre") {
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } throws RuntimeException("PDL er nede")
                    shouldThrow<RuntimeException> {
                        regler.kjerneregler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
        }

        Given("kompletteRegler") {
            When("bruker ikke finnes i PDL") {
                Then("gis tilgang") {
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } throws NotFoundRestException(URI.create("http://pdl"))
                    shouldNotThrowAny {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
            When("PDL-oppslag kaster annen exception") {
                Then("kastes exception videre") {
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } throws RuntimeException("PDL er nede")
                    shouldThrow<RuntimeException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
            When("enkelttilgang er registrert men motor kaster annen exception") {
                Then("kastes exception videre") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { enkeltTilgang.harTilgang(ansattId, funnetBruker.brukerId) } returns true
                    every { motor.kompletteRegler(any(), any()) } throws RuntimeException("noe gikk galt")
                    shouldThrow<RuntimeException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
            When("overstyrbar regel avslår og enkelttilgang er registrert") {
                Then("gis tilgang") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { motor.kompletteRegler(any(), any()) } throws regelException
                    every { motor.kjerneregler(any(), funnetBruker) } returns Unit
                    every { enkeltTilgang.harTilgang(ansattId, vanligBrukerId) } returns true
                    shouldNotThrowAny {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                    verifyOrder {
                        motor.kompletteRegler(any(), funnetBruker)
                        enkeltTilgang.harTilgang(ansattId, vanligBrukerId)
                        motor.kjerneregler(any(), funnetBruker)
                    }
                }
            }
            When("kjerneregler avslår og enkelttilgang er registrert") {
                Then("kastes kjerneavslaget videre til tross for enkelttilgang") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val ansatt = AnsattBuilder(ansattId).build()
                    val kjerneRegel = mockk<KjerneRegel>(relaxed = true)
                    val opprinneligAvslag = RegelException(ansatt, funnetBruker, kjerneRegel)
                    val kjerneAvslag = RegelException(ansatt, funnetBruker, kjerneRegel)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { motor.kompletteRegler(any(), funnetBruker) } throws opprinneligAvslag
                    every { motor.kjerneregler(any(), funnetBruker) } throws kjerneAvslag
                    every { enkeltTilgang.harTilgang(ansattId, vanligBrukerId) } returns true

                    shouldThrow<RegelException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    } shouldBe kjerneAvslag
                    verifyOrder {
                        enkeltTilgang.harTilgang(ansattId, vanligBrukerId)
                        motor.kjerneregler(any(), funnetBruker)
                    }
                }
            }
            When("kjerneregler kaster en teknisk feil") {
                Then("kastes feilen videre til tross for enkelttilgang") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                    val feil = IllegalStateException("Kjerneregler feilet")
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { motor.kompletteRegler(any(), funnetBruker) } throws regelException
                    every { motor.kjerneregler(any(), funnetBruker) } throws feil
                    every { enkeltTilgang.harTilgang(ansattId, vanligBrukerId) } returns true

                    shouldThrow<IllegalStateException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    } shouldBe feil
                    verifyOrder {
                        enkeltTilgang.harTilgang(ansattId, vanligBrukerId)
                        motor.kjerneregler(any(), funnetBruker)
                    }
                }
            }
            When("enkelttilgang mangler") {
                Then("kastes det opprinnelige avslaget videre uten å kjøre kjerneregler på nytt") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { motor.kompletteRegler(any(), funnetBruker) } throws regelException
                    every { enkeltTilgang.harTilgang(ansattId, vanligBrukerId) } returns false

                    shouldThrow<RegelException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    } shouldBe regelException
                    verify(exactly = 0) { motor.kjerneregler(any(), any()) }
                }
            }
            When("komplette regler godkjenner") {
                Then("kjøres ikke kjerneregler på nytt eller sjekkes enkelttilgang") {
                    val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns funnetBruker
                    every { motor.kompletteRegler(any(), funnetBruker) } returns Unit

                    shouldNotThrowAny {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                    verify(exactly = 0) { motor.kjerneregler(any(), any()) }
                    verify(exactly = 0) { enkeltTilgang.harTilgang(any(), any()) }
                }
            }
        }
    }
}
