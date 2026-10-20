package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.entraproxy.EnkeltTilgangMailSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.support.CronExpression
import org.springframework.test.util.ReflectionTestUtils.setField
import java.time.ZoneId
import java.time.ZonedDateTime

class EnkeltTilgangRapportørTest :
    BehaviorSpec({

        Given("rapportutsending på flere poder") {
            listOf(true, false).forEach { erLeder ->
                Then("sendes rapporten kun fra leder når erLeder er $erLeder") {
                    val mailer = mockk<EnkeltTilgangMailSender>(relaxed = true)
                    val rapportør = EnkeltTilgangRapportør(mailer)
                    setField(rapportør, "erLeder", erLeder)

                    rapportør.sendRapporter()

                    verify(exactly = if (erLeder) 1 else 0) { mailer.sendRapporter() }
                }
            }
        }
    })
