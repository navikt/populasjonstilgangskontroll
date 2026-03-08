package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.STRENGT_FORTROLIG_UTLAND
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.UGRADERT
import org.apache.kafka.clients.consumer.ConsumerRecord

class PdlGraderingFilterStrategyTest : DescribeSpec({

    val strategy = PdlGraderingFilterStrategy()

    fun record(gradering: Gradering?): ConsumerRecord<String, Personhendelse> {
        val hendelse = mockk<Personhendelse>(relaxed = true)
        every { hendelse.adressebeskyttelse } returns gradering?.let { Adressebeskyttelse(it) }
        return ConsumerRecord("topic", 0, 0L, "key", hendelse)
    }

    describe("filter") {

        it("filtrerer ikke bort STRENGT_FORTROLIG") {
            strategy.filter(record(STRENGT_FORTROLIG)) shouldBe false
        }

        it("filtrerer ikke bort STRENGT_FORTROLIG_UTLAND") {
            strategy.filter(record(STRENGT_FORTROLIG_UTLAND)) shouldBe false
        }

        it("filtrerer ikke bort FORTROLIG") {
            strategy.filter(record(FORTROLIG)) shouldBe false
        }

        it("filtrerer bort UGRADERT") {
            strategy.filter(record(UGRADERT)) shouldBe true
        }

        it("filtrerer bort hendelse uten adressebeskyttelse") {
            strategy.filter(record(null)) shouldBe true
        }
    }
})

