package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangKonsumentValidator.EnkeltTilgangKonsumentException

class EnkeltTilgangClientValidatorTest : BehaviorSpec({

    val cfg = EnkeltTilgangConfig()
    val validator = EnkeltTilgangKonsumentValidator(cfg)

    Given("EnkeltTilgangKonsumentValidator i prod") {
        beforeEach {
            mockkObject(ClusterUtils.Companion)
            every { ClusterUtils.isProd } returns true
        }
        afterEach {
            unmockkObject(ClusterUtils.Companion)
        }

        cfg.systemer.forEach { konsument ->
            When("konsument er godkjent ($konsument)") {
                Then("slipper gjennom uten exception") {
                    shouldNotThrowAny {
                        validator.valider(konsument)
                    }
                }
            }
        }
        When("konsument er ukjent") {
            Then("kaster EnkeltTilgangKonsumentException med tillatte systemer i melding") {
                val error = shouldThrow<EnkeltTilgangKonsumentException> {
                    validator.valider("ukjent-system")
                }
                error.message shouldContain "ukjent-system"
                cfg.systemer.forEach { system ->
                    error.message shouldContain system
                }
            }
        }
    }

    Given("EnkeltTilgangKonsumentValidator i ikke-prod") {
        beforeEach {
            mockkObject(ClusterUtils.Companion)
            every { ClusterUtils.isProd } returns false
        }
        afterEach {
            unmockkObject(ClusterUtils.Companion)
        }

        When("hvilken som helst konsument") {
            Then("slipper alt gjennom") {
                shouldNotThrowAny { validator.valider("ukjent-system") }
                shouldNotThrowAny { validator.valider("") }
                shouldNotThrowAny { validator.valider(cfg.systemer.first()) }
            }
        }
    }
})
