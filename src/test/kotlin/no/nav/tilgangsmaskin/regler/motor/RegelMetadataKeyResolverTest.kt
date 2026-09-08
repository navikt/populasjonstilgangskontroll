package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.messageSource
import org.springframework.context.support.ReloadableResourceBundleMessageSource

class RegelMetadataKeyResolverTest : BehaviorSpec({

    beforeSpec {
        messageSource = ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:regel-messages")
            setDefaultEncoding("UTF-8")
        }
    }

    Given("alle GruppeMetadata-entries") {
        GruppeMetadata.entries.forEach { gruppe ->
            val metadata = RegelMetadata(gruppe)

            Then("${gruppe.name} har begrunnelse som ikke er en nøkkel") {
                assertSoftly(metadata) {
                    begrunnelse shouldNotContain "regel."
                    begrunnelse.isNotBlank() shouldBe true
                }

            }

            Then("${gruppe.name} har kortNavn som ikke er en nøkkel") {
                assertSoftly(metadata) {
                    kortNavn shouldNotContain "regel."
                    kortNavn.isNotBlank() shouldBe true
                }
            }
        }
    }
})
