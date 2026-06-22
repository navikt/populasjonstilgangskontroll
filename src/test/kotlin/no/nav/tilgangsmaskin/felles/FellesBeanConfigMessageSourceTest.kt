package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.Locale

class FellesBeanConfigMessageSourceTest : BehaviorSpec({

    fun messageSource(vararg basenames: String) = ResourceBundleMessageSource().apply {
        setBasenames(*basenames)
        setDefaultEncoding("UTF-8")
    }

    Given("messageSource auto-konfigurert med spring.messages.basename") {
        When("en kjent message key fra messages.properties finnes") {
            Then("resolves key til forventet tekst") {
                val source = messageSource("messages")
                val key = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelException.kjerneregler"

                val resolved = source.getMessage(key, arrayOf("TESTREGEL", "Z123456", "12345678901"), Locale.getDefault())

                resolved shouldBe "Kjerneregel TESTREGEL er ikke overstyrbar, kan ikke overstyre tilgang for ansatt Z123456 til bruker 12345678901"
            }
        }

        When("en dev OpenAPI key fra openapi/dev/ resolves") {
            Then("ResourceBundleMessageSource finner key med directory-style basename") {
                val source = messageSource("openapi/dev/regel")
                val key = "openapi.dev.regel.tag.description"

                val resolved = source.getMessage(key, null, "NOT_FOUND", Locale.getDefault())

                resolved shouldNotBe "NOT_FOUND"
                resolved shouldBe "Denne kontrolleren skal kun brukes til testing"
            }
        }

        When("en prod OpenAPI key fra openapi-prod-tilgang resolves") {
            Then("ResourceBundleMessageSource finner key i root-basename") {
                val source = messageSource("openapi-prod-tilgang")
                val key = "openapi.tilgang.tag.description"

                val resolved = source.getMessage(key, null, "NOT_FOUND", Locale.getDefault())

                resolved shouldNotBe "NOT_FOUND"
            }
        }
    }
})
