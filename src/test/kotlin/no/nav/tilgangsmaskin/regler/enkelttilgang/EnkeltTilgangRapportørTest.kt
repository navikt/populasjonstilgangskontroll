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

class EnkeltTilgangRapportørTest : BehaviorSpec({
    Given("planlagt utsending av rapporter") {
        val planer = EnkeltTilgangRapportør::class.java.getMethod("sendRapporter").getAnnotationsByType(Scheduled::class.java)

        Then("kjøres rapporten hver morgen klokken 08 i Oslo") {
            val daglig = planer.single { it.cron.isNotEmpty() }
            daglig.zone shouldBe "Europe/Oslo"
            val cron = CronExpression.parse(daglig.cron)
            val fredag = ZonedDateTime.of(2026, 10, 23, 8, 0, 0, 0, ZoneId.of(daglig.zone))

            cron.next(fredag) shouldBe fredag.plusDays(1)
            cron.next(fredag.plusDays(1)) shouldBe fredag.plusDays(2)
        }

        Then("kjøres rapporten én gang to minutter etter oppstart") {
            val oppstart = planer.single { it.cron.isEmpty() }
            oppstart.initialDelayString shouldBe "2m"
            oppstart.fixedDelay shouldBe -1L
            oppstart.fixedRate shouldBe -1L
            oppstart.fixedDelayString shouldBe ""
            oppstart.fixedRateString shouldBe ""
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
