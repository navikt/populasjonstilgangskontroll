package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.springframework.context.support.ReloadableResourceBundleMessageSource

class RegelMetadataKeyResolverTest : BehaviorSpec({

    beforeSpec {
        RegelMetadata.messageSource = ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:regel-messages")
            defaultEncoding = "UTF-8"
        }
    }

    Given("alle GruppeMetadata-entries") {
        GruppeMetadata.entries.forEach { gruppe ->
            val metadata = RegelMetadata(gruppe)

            Then("${gruppe.name} har begrunnelse som ikke er en nøkkel") {
                metadata.begrunnelse shouldNotContain "regel."
                metadata.begrunnelse.isNotBlank().shouldBeTrue()
            }

            Then("${gruppe.name} har kortNavn som ikke er en nøkkel") {
                metadata.kortNavn shouldNotContain "regel."
                metadata.kortNavn.isNotBlank().shouldBeTrue()
            }
        }
    }
})
