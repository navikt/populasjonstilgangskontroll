package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.boot.health.contributor.Status.DOWN
import org.springframework.boot.health.contributor.Status.OUT_OF_SERVICE
import org.springframework.boot.health.contributor.Status.UP

class StatusAggregatorTest : DescribeSpec({

    val aggregator = FellesBeanConfig(mockk(relaxed = true)).outOfServiceIgnoringStatusAggregator()

    describe("outOfServiceIgnoringStatusAggregator") {
        it("returnerer DOWN når DOWN er i settet") {
            aggregator.getAggregateStatus(setOf(DOWN)) shouldBe DOWN
        }

        it("returnerer DOWN selv om andre statuser også er tilstede") {
            aggregator.getAggregateStatus(setOf(UP, DOWN)) shouldBe DOWN
        }

        it("returnerer UP for et tomt sett") {
            aggregator.getAggregateStatus(emptySet()) shouldBe UP
        }

        it("returnerer UP når bare UP er i settet") {
            aggregator.getAggregateStatus(setOf(UP)) shouldBe UP
        }

        it("ignorerer OUT_OF_SERVICE og returnerer UP") {
            aggregator.getAggregateStatus(setOf(OUT_OF_SERVICE)) shouldBe UP
        }
    }
})

