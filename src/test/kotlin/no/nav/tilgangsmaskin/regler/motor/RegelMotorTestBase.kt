package no.nav.tilgangsmaskin.regler.motor

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.notifikajon.LocalAuditor
import no.nav.tilgangsmaskin.felles.rest.PropertySettingTestContextInitializer
import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.security.TokenType
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
    protected lateinit var authContext: AuthContext

    init {
        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns brukerId
            every { vergemål.alle(any()) } returns emptySet()
            every { authContext.system } returns "test"
            every { authContext.type } returns TokenType.CCF
            every { authContext.systemNavn } returns "test"

            every { authContext.clusterAndSystem } returns "cluster:test"
        }
    }

    protected infix fun Ansatt.kanBehandle(bruker: Bruker) {
        shouldNotThrowAny {
            regelMotor.kompletteRegler(this, bruker)
        }
    }
}
