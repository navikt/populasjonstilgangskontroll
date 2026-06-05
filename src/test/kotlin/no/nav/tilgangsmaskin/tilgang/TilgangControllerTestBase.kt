package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.micrometer.core.instrument.Tags
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

abstract class TilgangControllerTestBase : BehaviorSpec() {

    @MockK
    protected lateinit var regelTjeneste: RegelTjeneste

    @MockK
    protected lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste

    @MockK(relaxed = true)
    protected lateinit var token: Token

    @MockK
    protected lateinit var teller: TokenTypeTeller

    protected val ansattId = AnsattId("Z999999")
    protected val brukerId = "08526835670"

    protected lateinit var mockMvc: MockMvc

    init {
        beforeSpec { MockKAnnotations.init(this@TilgangControllerTestBase) }

        beforeEach {
            clearAllMocks()
            mockMvc = standaloneSetup(TilgangController(regelTjeneste, enkeltTilgangTjeneste, token, TokenTypeGuard(token), teller))
                .setValidator(LocalValidatorFactoryBean().also { it.afterPropertiesSet() })
                .build()
            justRun { teller.tell(any<Tags>()) }
            every { token.ansattId } returns ansattId
        }
    }
}
