package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.samskivert.mustache.Mustache
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EnkeltTilgangMailSender.Companion.varighet
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgang
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.INGEN_LEDER
import no.nav.tilgangsmaskin.regler.enkelttilgang.LederEnkeltTilganger
import java.time.Instant

class EnkeltTilgangMailSenderTest : BehaviorSpec({
    Given("filtrering av begrunnelser i e-postrapporten") {
        listOf(
            "Oppgave (12345) skal behandles" to 0,
            "Oppgave" to 1,
            "Oppgave 12345 skal behandles" to 1,
            "Behandling av Oppgave 12345" to 1,
            "Behandling av Oppgave (12345)" to 1,
            "oppgave (12345) skal behandles" to 1,
            "oppgave 12345 skal behandles" to 1,
        ).forEach { (begrunnelse, antallEposter) ->
            Then("sendes $antallEposter e-poster for begrunnelsen '$begrunnelse'") {
                val enkelt = mockk<EnkeltTilgangTjeneste>()
                val proxy = mockk<EntraProxyTjeneste>(relaxed = true)
                val tilgang =
                    EnkeltTilgang(
                        AnsattId("Z123456"),
                        begrunnelse,
                        Instant.parse("2026-10-19T10:00:00Z"),
                        null,
                        null,
                        null,
                        "0219",
                        Instant.parse("2026-10-20T21:59:00Z"),
                        1L,
                    )
                every { enkelt.ikkeRapportertePrLeder(setOf("10108000398")) } returns
                    listOf(
                        LederEnkeltTilganger(
                            Leder("leder@nav.no", AnsattId("Z123457"), "Leder"),
                            listOf(tilgang),
                        ),
                    )
                EnkeltTilgangMailSender(enkelt, proxy, Mustache.compiler()).sendRapporter()

                verify(exactly = antallEposter) { proxy.mail(any()) }
                verify(exactly = 0) { enkelt.markerRapportert(any()) }
            }
        }
    }

    Given("henting av data til e-postrapporten") {
        Then("ekskluderes det angitte fødselsnummeret fra oppslaget") {
            val enkelt = mockk<EnkeltTilgangTjeneste>()
            val proxy = mockk<EntraProxyTjeneste>()
            every { enkelt.ikkeRapportertePrLeder(setOf("10108000398")) } returns emptyList()
            val sender = EnkeltTilgangMailSender(enkelt, proxy, Mustache.compiler())

            sender.sendRapporter()

            verify(exactly = 1) { enkelt.ikkeRapportertePrLeder(setOf("10108000398")) }
            verify { proxy wasNot Called }
        }
    }

    Given("rapportering av sendte e-poster") {
        listOf(false, true).forEach { feilerFørsteEpost ->
            Then("behandles begge rapporter uten databasemarkering når første e-post feiler=$feilerFørsteEpost") {
                val enkelt = mockk<EnkeltTilgangTjeneste>()
                val proxy = mockk<EntraProxyTjeneste>(relaxed = true)
                val tilgang =
                    EnkeltTilgang(
                        AnsattId("Z123456"),
                        "Begrunnelse for tilgang",
                        Instant.parse("2026-10-19T10:00:00Z"),
                        null,
                        null,
                        null,
                        "0219",
                        Instant.parse("2026-10-20T21:59:00Z"),
                        1L,
                    )
                val annenTilgang = tilgang.copy(databaseId = 2L)
                val annenAnsatt = tilgang.copy(id = AnsattId("Z123458"), databaseId = 3L)
                val annenEnhet = tilgang.copy(enhet = "0105", databaseId = 4L)
                val oppgave = tilgang.copy(begrunnelse = "Oppgave (12345)", databaseId = 5L)
                val utenLeder = tilgang.copy(databaseId = 6L)
                every { enkelt.ikkeRapportertePrLeder(any()) } returns
                    listOf(
                        LederEnkeltTilganger(
                            Leder("leder@nav.no", AnsattId("Z123457"), "Leder"),
                            listOf(tilgang, annenTilgang, annenEnhet, oppgave),
                        ),
                        LederEnkeltTilganger(
                            Leder("annenleder@nav.no", AnsattId("Z123459"), "Annen leder"),
                            listOf(annenAnsatt),
                        ),
                        LederEnkeltTilganger(INGEN_LEDER, listOf(utenLeder)),
                    )
                if (feilerFørsteEpost) {
                    var antallKall = 0
                    every { proxy.mail(any()) } answers {
                        if (antallKall++ == 0) throw IllegalStateException("Kunne ikke sende e-post")
                        Unit
                    }
                }

                EnkeltTilgangMailSender(enkelt, proxy, Mustache.compiler()).sendRapporter()

                verify(exactly = 2) { proxy.mail(any()) }
                verify(exactly = 0) { enkelt.markerRapportert(any()) }
            }
        }
    }

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
