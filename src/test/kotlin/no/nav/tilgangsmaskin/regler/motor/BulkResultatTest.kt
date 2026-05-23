package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.springframework.context.support.StaticMessageSource
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

class BulkResultatTest : BehaviorSpec({

    beforeSpec {
        RegelMetadata.messageSource = StaticMessageSource()
    }

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val bruker = BrukerBuilder(brukerId).build()

    Given("BulkResultat.ok") {
        When("opprettet via companion-funksjon") {
            Then("har status NO_CONTENT og ingen regel") {
                val resultat = BulkResultat.ok(bruker)
                resultat.status shouldBe NO_CONTENT
                resultat.bruker shouldBe bruker
                resultat.regel shouldBe null
            }
        }
    }

    Given("BulkResultat.avvist") {
        When("opprettet med RegelException") {
            Then("har status FORBIDDEN og referanse til regel") {
                val ansatt = AnsattBuilder(ansattId).build()
                val regel = StrengtFortroligRegel()
                val exception = RegelException(ansatt, bruker, regel)
                val resultat = BulkResultat.avvist(bruker, exception)
                resultat.status shouldBe FORBIDDEN
                resultat.bruker shouldBe bruker
                resultat.regel shouldBe regel
            }
        }
    }
})
