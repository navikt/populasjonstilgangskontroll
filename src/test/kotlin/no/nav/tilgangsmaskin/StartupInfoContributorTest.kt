package no.nav.tilgangsmaskin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.regler.motor.EgneDataRegel
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import org.springframework.boot.actuate.info.Info
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

class StartupInfoContributorTest : DescribeSpec({

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

    describe("info") {

        it("inneholder Cluster") {
            @Suppress("UNCHECKED_CAST")
            val info = build()["info"] as Map<String, Any?>
            info.containsKey("Cluster") shouldBe true
        }

        it("inneholder Startup") {
            @Suppress("UNCHECKED_CAST")
            val info = build()["info"] as Map<String, Any?>
            info.containsKey("Startup") shouldBe true
        }

        it("inneholder Name med riktig verdi") {
            @Suppress("UNCHECKED_CAST")
            val info = build()["info"] as Map<String, Any?>
            info["Name"] shouldBe "test-app"
        }
    }

    describe("dev-info utenfor prod") {

        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns false
        }

        afterEach { unmockkObject(ClusterUtils) }

        it("inneholder dev-info") {
            build().containsKey("dev-info") shouldBe true
        }

        it("dev-info inneholder Client ID") {
            @Suppress("UNCHECKED_CAST")
            val devInfo = build()["dev-info"] as Map<String, Any?>
            devInfo["Client ID"] shouldBe "client-id"
        }

        it("dev-info inneholder Java version") {
            @Suppress("UNCHECKED_CAST")
            val devInfo = build()["dev-info"] as Map<String, Any?>
            devInfo["Java version"] shouldBe "25"
        }
    }

    describe("dev-info i prod") {

        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns true
        }

        afterEach { unmockkObject(ClusterUtils) }

        it("inneholder ikke dev-info") {
            build().containsKey("dev-info") shouldBe false
        }
    }

    describe("regelsett") {

        it("legger til detalj per regelsett med beskrivelse som nøkkel") {
            val rs = regelSett(EgneDataRegel())
            build(rs).containsKey(rs.beskrivelse) shouldBe true
        }

        it("regelsett-detalj inneholder regel-navn") {
            val regel = EgneDataRegel()
            val rs = regelSett(regel)
            @Suppress("UNCHECKED_CAST")
            val detalj = build(rs)[rs.beskrivelse] as List<*>
            detalj.any { it.toString().contains(regel.kortNavn) } shouldBe true
        }

        it("ingen regelsett-detaljer når ingen regelsett er gitt") {
            build().containsKey("kjerneregelsett") shouldBe false
        }
    }
})

