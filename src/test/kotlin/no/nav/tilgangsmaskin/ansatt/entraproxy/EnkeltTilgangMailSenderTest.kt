package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EnkeltTilgangMailSender.Companion.varighet
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.regler.enkelttilgang.INGEN_LEDER
import no.nav.tilgangsmaskin.regler.enkelttilgang.LederEnkeltTilganger
import java.time.Instant

class EnkeltTilgangMailSenderTest : BehaviorSpec({
    Given("varighet i e-postrapporten") {
        val created = Instant.parse("2026-10-19T10:00:00Z")

        Then("vises Ut dagen ved utløp samme dato") {
            varighet(created, Instant.parse("2026-10-19T21:59:00Z")) shouldBe "Ut dagen"
        }

        Then("telles dagen etter opprettelse som første dag") {
            varighet(created, Instant.parse("2026-10-20T21:59:00Z")) shouldBe "1 dag"
            varighet(created, Instant.parse("2026-10-22T21:59:00Z")) shouldBe "3 dager"
        }

        Then("brukes kalenderdato i Oslo fremfor antall hele døgn") {
            varighet(Instant.parse("2026-10-19T21:30:00Z"), Instant.parse("2026-10-19T22:30:00Z")) shouldBe "1 dag"
        }

        Then("telles kalenderdager over overgang til vintertid") {
            varighet(Instant.parse("2026-10-24T10:00:00Z"), Instant.parse("2026-10-26T22:59:00Z")) shouldBe "2 dager"
        }

        Then("vises tom varighet når opprettelsestidspunkt mangler") {
            varighet(null, Instant.parse("2026-10-20T21:59:00Z")) shouldBe ""
        }
    }

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
