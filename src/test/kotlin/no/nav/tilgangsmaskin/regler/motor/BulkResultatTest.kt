package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.avvist
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.ok
import org.springframework.context.support.StaticMessageSource
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

class BulkResultatTest : BehaviorSpec({

    beforeSpec {
        RegelMetadata.messageSource = StaticMessageSource()
    }

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val b = BrukerBuilder(brukerId).build()

    Given("BulkResultat.ok") {
        When("opprettet via companion-funksjon") {
            Then("har status NO_CONTENT og ingen regel") {
                val resultat = ok(b)
                assertSoftly(resultat) {
                    status shouldBe NO_CONTENT
                    bruker shouldBe b
                    regel shouldBe null
                }

            }
        }
    }

    Given("BulkResultat.avvist") {
        When("opprettet med RegelException") {
            Then("har status FORBIDDEN og referanse til regel") {
                val ansatt = AnsattBuilder(ansattId).build()
                val r = StrengtFortroligRegel()
                val exception = RegelException(ansatt, b, r)
                val resultat = avvist(b, exception)
                assertSoftly(resultat) {
                    status shouldBe FORBIDDEN
                    bruker shouldBe b
                    regel shouldBe r
                }
            }
        }
    }
})
