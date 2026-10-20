package no.nav.tilgangsmaskin.regler.enkelttilgang

import com.samskivert.mustache.Mustache
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangRapportGenerator.Companion.varighet
import java.time.Instant

class EnkeltTilgangMailSenderTest :
    BehaviorSpec({
        Given("generering av HTML-rapport") {
            Then("gjengis leder og formaterte tilgangsrader med vekslende farger") {
                val tilgang =
                    EnkeltTilgang(
                        AnsattId("Z123456"),
                        "Begrunnelse for tilgang",
                        Instant.parse("2026-10-19T10:00:00Z"),
                        "0301",
                        "prod-fss:isa:gosys",
                        "Oslo",
                        "0219",
                        Instant.parse("2026-10-20T21:59:00Z"),
                        1L,
                    )
                val rapport =
                    LederEnkeltTilganger(
                        Leder("leder@nav.no", AnsattId("Z123457"), "Leder"),
                        listOf(tilgang, tilgang),
                    )

                val html = requireNotNull(EnkeltTilgangRapportGenerator(Mustache.compiler(), mockk()).lagRapporter(rapport)).html

                html shouldContain "Hei Leder,"
                html shouldContain "Z123456"
                html shouldContain "19.10.26"
                html shouldContain "12:00"
                html shouldContain "1 dag"
                html shouldContain "Oslo"
                html shouldContain "Begrunnelse for tilgang"
                html shouldContain "Gosys personoppslag"
                html shouldContain """bgcolor="#d3dfd7""""
                html shouldContain """bgcolor="#c5d4cb""""
            }
        }

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
                    val rapporter = EnkeltTilgangRapportGenerator(Mustache.compiler(), enkelt).lagRapporter()
                    val sender = EnkeltTilgangMailSender(proxy)
                    rapporter.forEach(sender::sendRapport)

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
                val sender = EnkeltTilgangMailSender(proxy)

                EnkeltTilgangRapportGenerator(Mustache.compiler(), enkelt).lagRapporter().forEach(sender::sendRapport)

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

                    val rapporter = EnkeltTilgangRapportGenerator(Mustache.compiler(), enkelt).lagRapporter()
                    val sender = EnkeltTilgangMailSender(proxy)
                    rapporter.forEach(sender::sendRapport)

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
                val medLeder = LederEnkeltTilganger(Leder("leder@nav.no", AnsattId("Z123456"), "Leder"), listOf(tilgang))
                val ingenLeder = LederEnkeltTilganger(INGEN_LEDER, emptyList())
                val tomRapport = LederEnkeltTilganger(medLeder.leder, emptyList())

                val generator = EnkeltTilgangRapportGenerator(Mustache.compiler(), mockk())
                generator.lagRapporter(ingenLeder) shouldBe null
                generator.lagRapporter(tomRapport) shouldBe null
                requireNotNull(generator.lagRapporter(medLeder)).databaseIds shouldBe setOf(1L)

                val nyere = tilgang.copy(created = tilgang.created?.plusSeconds(60), databaseId = 2L)
                val utenDato = tilgang.copy(created = null, databaseId = 3L)
                val annenEnhet = tilgang.copy(enhet = "0105", databaseId = 4L)
                val oppgave = tilgang.copy(begrunnelse = "Oppgave (12345)", databaseId = 5L)
                val ufiltrert = LederEnkeltTilganger(medLeder.leder, listOf(utenDato, nyere, annenEnhet, oppgave, tilgang))

                generator.lagRapporter(ufiltrert) shouldBe
                    generator.lagRapporter(LederEnkeltTilganger(medLeder.leder, listOf(tilgang, nyere, utenDato)))
                requireNotNull(generator.lagRapporter(ufiltrert)).databaseIds shouldBe setOf(1L, 2L, 3L)
            }
        }
    })
