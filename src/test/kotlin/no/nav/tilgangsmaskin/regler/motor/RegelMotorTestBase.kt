package no.nav.tilgangsmaskin.regler.motor

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.LocalAuditor
import no.nav.tilgangsmaskin.felles.rest.PropertySettingTestContextInitializer
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration

@AutoConfigureMetrics
@ContextConfiguration(initializers = [PropertySettingTestContextInitializer::class], classes = [LocalAuditor::class])
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
abstract class RegelMotorTestBase(
    protected val regelMotor: RegelMotor,
) : BehaviorSpec() {
    protected val brukerId = BrukerId("08526835670")
    protected val ansattId = AnsattId("Z999999")

    @MockkBean
    protected lateinit var oppfølging: OppfølgingTjeneste

    @MockkBean
    protected lateinit var proxy: EntraProxyTjeneste

    @MockkBean
    protected lateinit var vergemål: VergemålTjeneste

    @MockkBean
    protected lateinit var nom: NomTjeneste

    @MockkBean
    protected lateinit var token: Token

    init {
        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns brukerId
            every { vergemål.alle(any()) } returns emptySet()
            every { token.system } returns "test"
            every { token.type } returns TokenType.CCF
            every { token.systemNavn } returns "test"
            every { token.clusterAndSystem } returns "cluster:test"
        }
    }

    protected inline fun <reified T : Regel> forventAvvistAv(ansatt: Ansatt, bruker: Bruker) {
        shouldThrow<RegelException> {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.regel.shouldBeInstanceOf<T>()
    }

    protected infix fun Ansatt.kanBehandle(bruker: Bruker) {
        shouldNotThrowAny {
            regelMotor.kompletteRegler(this, bruker)
        }
    }
}
