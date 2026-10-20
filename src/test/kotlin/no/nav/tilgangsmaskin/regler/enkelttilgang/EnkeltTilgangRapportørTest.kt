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

        Given("planlagt utsending av rapporter") {
            Then("kjøres rapporten hver tirsdag klokken 10:45 i Oslo") {
                val plan = EnkeltTilgangRapportør::class.java
                    .getMethod("sendRapporter")
                    .getAnnotation(Scheduled::class.java)

                plan.zone shouldBe "Europe/Oslo"
                val cron = CronExpression.parse(plan.cron)
                val tirsdag = ZonedDateTime.of(2026, 10, 20, 10, 44, 0, 0, ZoneId.of(plan.zone))

                cron.next(tirsdag) shouldBe tirsdag.plusMinutes(1)
                cron.next(tirsdag.plusMinutes(1)) shouldBe tirsdag.plusWeeks(1).plusMinutes(1)
            }
        }

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
