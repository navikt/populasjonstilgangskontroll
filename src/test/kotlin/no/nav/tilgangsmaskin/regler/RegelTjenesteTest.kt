package no.nav.tilgangsmaskin.regler

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import java.net.URI

class RegelTjenesteTest : DescribeSpec() {

    @MockK
    lateinit var motor: RegelMotor
    @MockK
    lateinit var brukere: BrukerTjeneste
    @MockK
    lateinit var ansatte: AnsattTjeneste
    @MockK
    lateinit var overstyring: OverstyringTjeneste

    init {
        val vanligBrukerId = BrukerId("08526835670")
        val ansattId = AnsattId("Z999999")

        lateinit var regler: RegelTjeneste

        beforeSpec {
            MockKAnnotations.init(this@RegelTjenesteTest)
        }

        beforeEach {
            clearAllMocks()
            every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
            regler = RegelTjeneste(motor, brukere, ansatte, overstyring)
        }

        describe("bulk") {

            it("avvist bruker havner i godkjente når overstyring er registrert") {
                val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(funnetBruker)
                every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.avvist(funnetBruker, regelException))
                every { overstyring.overstyringer(any(), any()) } returns listOf(vanligBrukerId)

                val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))

                resultater.godkjente.shouldBe(setOf(resultater.godkjente.single()))
                resultater.godkjente.single().brukerId shouldBe vanligBrukerId.verdi
                resultater.avviste.shouldBeEmpty()
                resultater.ukjente.shouldBeEmpty()
            }

            it("avvist bruker havner i avviste når ingen overstyring er registrert") {
                val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                val regelException = RegelException(AnsattBuilder(ansattId).build(), funnetBruker, mockk<Regel>(relaxed = true))
                every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(funnetBruker)
                every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.avvist(funnetBruker, regelException))
                every { overstyring.overstyringer(any(), any()) } returns emptyList()

                val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))

                resultater.avviste.single().brukerId shouldBe vanligBrukerId.verdi
                resultater.godkjente.shouldBeEmpty()
                resultater.ukjente.shouldBeEmpty()
            }

            it("id som ikke finnes i PDL får tilgang") {
                val ikkeFunnetId = BrukerId("11111111111")
                val funnetBruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukere(setOf(vanligBrukerId.verdi, ikkeFunnetId.verdi)) } returns setOf(funnetBruker)
                every { motor.bulkRegler(any(), any()) } returns setOf(BulkResultat.ok(funnetBruker))
                every { overstyring.overstyringer(any(), any()) } returns emptyList()

                val resultater = regler.bulkRegler(ansattId,
                    setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi), BrukerIdOgRegelsett(ikkeFunnetId.verdi)))

                resultater.godkjente.map { it.brukerId } shouldContainExactlyInAnyOrder
                    listOf(vanligBrukerId.verdi, ikkeFunnetId.verdi)
                resultater.avviste.shouldBeEmpty()
                resultater.ukjente.shouldBeEmpty()
            }
        }

        describe("kjerneregler") {

            it("tilgang gis når bruker ikke finnes i PDL") {
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } throws
                    NotFoundRestException(URI.create("http://pdl"))

                shouldNotThrowAny { regler.kjerneregler(ansattId, vanligBrukerId.verdi) }
            }
        }

        describe("kompletteRegler") {

            it("tilgang gis når bruker ikke finnes i PDL") {
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } throws
                    NotFoundRestException(URI.create("http://pdl"))

                shouldNotThrowAny { regler.kompletteRegler(ansattId, vanligBrukerId.verdi) }
            }
        }
    }
}