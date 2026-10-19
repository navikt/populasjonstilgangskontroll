package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.regler.enkelttilgang.INGEN_LEDER
import no.nav.tilgangsmaskin.regler.enkelttilgang.LederEnkeltTilganger

class EnkeltTilgangMailSenderTest : BehaviorSpec({
    Given("et kjent system") {
        Then("returneres det tilpassede visningsnavnet") {
            visningsnavnForSystem("prod-fss:isa:gosys") shouldBe "Gosys personoppslag"
            visningsnavnForSystem("prod-fss:histark:histark-core-api") shouldBe "Histark"
            visningsnavnForSystem("prod-gcp:oppgavehandtering:gosys-oppgavebehandling-api") shouldBe "Gosys oppgaveliste"
        }
    }

    Given("et ukjent system") {
        Then("returneres systemnavnet slik det gjorde før") {
            visningsnavnForSystem("prod-gcp:team:ukjent-api") shouldBe "ukjent-api"
        }
    }

    Given("system ikke er oppgitt") {
        Then("returneres en tom streng") {
            visningsnavnForSystem(null) shouldBe ""
        }
    }

    Given("rapporter med og uten leder") {
        Then("filtreres rapporten for INGEN_LEDER vekk") {
            val medLeder = LederEnkeltTilganger(Leder("leder@nav.no", AnsattId("Z123456"), "Leder"), emptyList())
            val ingenLeder = LederEnkeltTilganger(INGEN_LEDER, emptyList())

            rapporterMedLeder(listOf(ingenLeder, medLeder)) shouldBe listOf(medLeder)
        }
    }
})
