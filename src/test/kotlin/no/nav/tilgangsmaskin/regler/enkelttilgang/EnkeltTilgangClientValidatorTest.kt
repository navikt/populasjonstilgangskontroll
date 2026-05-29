package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec

class EnkeltTilgangClientValidatorTest : BehaviorSpec({

    val cfg = EnkeltTilgangConfig()
    val prodValidator = EnkeltTilgangProdClientValidator(cfg)
    val devValidator = EnkeltTilgangDevClientValidator()

    Given("EnkeltTilgangProdClientValidator") {
        cfg.systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("slipper gjennom uten exception") {
                    shouldNotThrowAny { prodValidator.valider(konsument) }
                }
            }
        }
        When("konsument er ukjent") {
            Then("kaster EnkeltTilgangException") {
                shouldThrow<EnkeltTilgangException> {
                    prodValidator.valider("ukjent-system")
                }
            }
        }
    }

    Given("EnkeltTilgangDevClientValidator (fallback)") {
        When("hvilken som helst konsument") {
            Then("slipper alt gjennom") {
                shouldNotThrowAny { devValidator.valider("ukjent-system") }
                shouldNotThrowAny { devValidator.valider("") }
                shouldNotThrowAny { devValidator.valider(cfg.systemer.first()) }
            }
        }
    }
})
