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

        Then("kjøres rapporten hver mandag klokken 08 i Oslo") {
            val ukentlig = planer.single { it.cron.isNotEmpty() }
            ukentlig.zone shouldBe "Europe/Oslo"
            val cron = CronExpression.parse(ukentlig.cron)
            val fredag = ZonedDateTime.of(2026, 10, 23, 8, 0, 0, 0, ZoneId.of(ukentlig.zone))

            val mandag = fredag.plusDays(3)
            cron.next(fredag) shouldBe mandag
            cron.next(mandag) shouldBe mandag.plusWeeks(1)
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
