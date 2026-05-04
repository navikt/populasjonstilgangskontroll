package no.nav.tilgangsmaskin

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.regler.motor.EgneDataRegel
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import org.springframework.boot.actuate.info.Info
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

@Suppress("UNCHECKED_CAST")
class StartupInfoContributorTest : BehaviorSpec({

    val env = mockk<ConfigurableEnvironment>()
    val ctx = mockk<ConfigurableApplicationContext>()

    every { ctx.environment } returns env
    every { ctx.startupDate } returns 0L
    every { env.getProperty("spring.application.name") } returns "test-app"
    every { env.getProperty("azure.app.client.id") } returns "client-id"
    every { env.getProperty("java.version") } returns "25"
    every { env.getProperty("java.runtime.version") } returns "25.0.1"
    every { env.getProperty("java.vm.vendor") } returns "OpenJDK"

    fun build(vararg regelsett: RegelSett): Map<String, Any> {
        val contributor = StartupInfoContributor(ctx, *regelsett)
        val builder = Info.Builder()
        contributor.contribute(builder)
        return builder.build().details
    }

    fun regelSett(vararg regler: Regel) =
        RegelSett(KJERNE_REGELTYPE to regler.toList())

    Given("info") {
        When("contributor bygger info") {
            Then("inneholder Cluster") {
                val info = build()["info"] as Map<String, Any?>
                info.containsKey("Cluster") shouldBe true
            }
            Then("inneholder Startup") {
                val info = build()["info"] as Map<String, Any?>
                info.containsKey("Startup") shouldBe true
            }
            Then("inneholder Name med riktig verdi") {
                val info = build()["info"] as Map<String, Any?>
                info["Name"] shouldBe "test-app"
            }
        }
    }

    Given("dev-info utenfor prod") {
        beforeEach {
            mockkObject(ClusterUtils)
            every { isProd } returns false
        }
        afterEach { unmockkObject(ClusterUtils) }

        When("contributor bygger info") {
            Then("inneholder dev-info") {
                build().containsKey("dev-info") shouldBe true
            }
            Then("dev-info inneholder Client ID") {
                val devInfo = build()["dev-info"] as Map<String, Any?>
                devInfo["Client ID"] shouldBe "client-id"
            }
            Then("dev-info inneholder Java version") {
                val devInfo = build()["dev-info"] as Map<String, Any?>
                devInfo["Java version"] shouldBe "25"
            }
        }
    }

    Given("dev-info i prod") {
        beforeEach {
            mockkObject(ClusterUtils)
            every { isProd } returns true
        }
        afterEach { unmockkObject(ClusterUtils) }

        When("contributor bygger info") {
            Then("inneholder ikke dev-info") {
                build().containsKey("dev-info") shouldBe false
            }
        }
    }

    Given("regelsett") {
        When("ett regelsett er gitt") {
            Then("legges det til en detalj med beskrivelse som nøkkel") {
                val rs = regelSett(EgneDataRegel())
                build(rs).containsKey(rs.beskrivelse) shouldBe true
            }
            Then("regelsett-detalj inneholder regel-navn") {
                val regel = EgneDataRegel()
                val rs = regelSett(regel)
                val detalj = build(rs)[rs.beskrivelse] as List<*>
                detalj.any { it.toString().contains(regel.kortNavn) } shouldBe true
            }
        }
    }
})
