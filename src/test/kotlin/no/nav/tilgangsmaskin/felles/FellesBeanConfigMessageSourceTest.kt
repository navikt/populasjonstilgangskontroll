package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import java.util.Locale

class FellesBeanConfigMessageSourceTest : BehaviorSpec({

    val config = FellesBeanConfig(mockk<ConsumerAwareHandlerInterceptor>(relaxed = true))

    Given("messageSource") {
        When("basenames konfigureres") {
            Then("inneholder basenameSet forventede classpath-entries") {
                val source = config.messageSource(listOf("messages", "regel-messages"))
                source.basenameSet shouldContain "classpath:messages"
                source.basenameSet shouldContain "classpath:regel-messages"
            }
        }

        When("en kjent message key finnes") {
            Then("resolves key til forventet tekst") {
                val source = config.messageSource(listOf("messages"))
                val key = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelException.kjerneregler"

                val resolved = source.getMessage(
                    key,
                    arrayOf("TESTREGEL", "Z123456", "12345678901"),
                    Locale.getDefault()
                )

                resolved shouldBe "Kjerneregel TESTREGEL er ikke overstyrbar, kan ikke overstyre tilgang for ansatt Z123456 til bruker 12345678901"
            }
        }
    }
})
