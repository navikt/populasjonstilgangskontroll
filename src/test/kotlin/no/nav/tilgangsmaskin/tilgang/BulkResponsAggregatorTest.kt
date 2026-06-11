package no.nav.tilgangsmaskin.tilgang

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BrukerOgRegelsett
import no.nav.tilgangsmaskin.regler.BulkResponsAggregator
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.avvist
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.ok
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

class BulkResponsAggregatorTest : BehaviorSpec({

    val enkeltTilgangTjeneste = mockk<EnkeltTilgangTjeneste>()
    val auditor = mockk<Auditor>(relaxed = true)
    val aggregator = BulkResponsAggregator(enkeltTilgangTjeneste, auditor)

    val ansattId = AnsattId("Z999999")
    val ansatt = AnsattBuilder(ansattId).build()
    val brukerId1 = BrukerId("08526835670")
    val brukerId2 = BrukerId("08526835644")
    val brukerId3 = BrukerId("20478606614")

    val bruker1 = BrukerBuilder(brukerId1).build()
    val bruker2 = BrukerBuilder(brukerId2).build()
    val bruker3 = BrukerBuilder(brukerId3).build()

    val testRegel = mockk<Regel>(relaxed = true)

    Given("alle resultater er godkjent") {
        val resultater = setOf(
            ok(bruker1),
            ok(bruker2)
        )
        val oppgitt = setOf(
            BrukerIdOgRegelsett(brukerId1.verdi, KOMPLETT_REGELTYPE),
            BrukerIdOgRegelsett(brukerId2.verdi, KOMPLETT_REGELTYPE)
        )
        val brukere = setOf(
            BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE),
            BrukerOgRegelsett(bruker2, KOMPLETT_REGELTYPE)
        )

        every { enkeltTilgangTjeneste.tilganger(ansattId, emptySet()) } returns emptySet()

        When("aggreger kalles") {
            val respons = aggregator.aggreger(ansattId, ansatt, resultater, oppgitt, brukere)

            Then("alle er godkjent") {
                assertSoftly(respons) {
                    godkjente shouldHaveSize 2
                    avviste.shouldBeEmpty()
                    ukjente.shouldBeEmpty()
                }
            }
        }
    }

    Given("en bruker er avvist og har ingen enkelttilgang") {
        val resultater = setOf(
            ok(bruker1),
            avvist(bruker2, RegelException(ansatt, bruker2, testRegel))
        )
        val oppgitt = setOf(
            BrukerIdOgRegelsett(brukerId1.verdi, KOMPLETT_REGELTYPE),
            BrukerIdOgRegelsett(brukerId2.verdi, KOMPLETT_REGELTYPE)
        )
        val brukere = setOf(
            BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE),
            BrukerOgRegelsett(bruker2, KOMPLETT_REGELTYPE)
        )

        every { enkeltTilgangTjeneste.tilganger(ansattId, setOf(brukerId2)) } returns emptySet()

        When("aggreger kalles") {
            val respons = aggregator.aggreger(ansattId, ansatt, resultater, oppgitt, brukere)

            Then("en er godkjent og en er avvist") {
                assertSoftly(respons) {
                    godkjente shouldHaveSize 1
                    godkjente.first().brukerId shouldBe brukerId1.verdi
                    avviste shouldHaveSize 1
                    avviste.first().brukerId shouldBe brukerId2.verdi
                    avviste.first().status shouldBe FORBIDDEN.value()
                }
            }
        }
    }

    Given("en bruker er avvist men har enkelttilgang") {
        val resultater = setOf(
            ok(bruker1),
            avvist(bruker2, RegelException(ansatt, bruker2, testRegel))
        )
        val oppgitt = setOf(
            BrukerIdOgRegelsett(brukerId1.verdi, KOMPLETT_REGELTYPE),
            BrukerIdOgRegelsett(brukerId2.verdi, KOMPLETT_REGELTYPE)
        )
        val brukere = setOf(
            BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE),
            BrukerOgRegelsett(bruker2, KOMPLETT_REGELTYPE)
        )

        every { enkeltTilgangTjeneste.tilganger(ansattId, setOf(brukerId2)) } returns setOf(brukerId2)

        When("aggreger kalles") {
            val respons = aggregator.aggreger(ansattId, ansatt, resultater, oppgitt, brukere)

            Then("begge er godkjent") {
                assertSoftly(respons) {
                    godkjente shouldHaveSize 2
                    avviste.shouldBeEmpty()
                }
            }
        }
    }

    Given("en bruker ikke finnes i PDL") {
        val resultater = setOf(ok(bruker1))
        val oppgitt = setOf(
            BrukerIdOgRegelsett(brukerId1.verdi, KOMPLETT_REGELTYPE),
            BrukerIdOgRegelsett(brukerId3.verdi, KOMPLETT_REGELTYPE)
        )
        val brukere = setOf(BrukerOgRegelsett(bruker1, KOMPLETT_REGELTYPE))

        every { enkeltTilgangTjeneste.tilganger(ansattId, emptySet()) } returns emptySet()

        When("aggreger kalles") {
            val respons = aggregator.aggreger(ansattId, ansatt, resultater, oppgitt, brukere)

            Then("ikke-funnet bruker gis tilgang (ok)") {
                assertSoftly(respons) {
                    this.resultater shouldHaveSize 2
                    avviste.shouldBeEmpty()
                    this.resultater.all { it.status == NO_CONTENT.value() } shouldBe true
                    this.resultater.map { it.brukerId } shouldContainExactlyInAnyOrder listOf(brukerId1.verdi, brukerId3.verdi)
                }
            }
        }
    }

    Given("tom resultatliste") {
        val resultater = emptySet<BulkResultat>()
        val oppgitt = emptySet<BrukerIdOgRegelsett>()
        val brukere = emptySet<BrukerOgRegelsett>()

        When("aggreger kalles") {
            val respons = aggregator.aggreger(ansattId, ansatt, resultater, oppgitt, brukere)

            Then("returnerer tom respons") {
                assertSoftly(respons) {
                    ansattId shouldBe ansattId
                    resultater.shouldBeEmpty()
                }
            }
        }
    }
})

