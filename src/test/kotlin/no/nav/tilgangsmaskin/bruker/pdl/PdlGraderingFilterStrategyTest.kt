package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
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

class PdlGraderingFilterStrategyTest : BehaviorSpec({

    val strategy = PdlGraderingFilterStrategy()

    fun record(gradering: Gradering?): ConsumerRecord<String, Personhendelse> {
        val hendelse = mockk<Personhendelse>(relaxed = true)
        every { hendelse.adressebeskyttelse } returns gradering?.let { Adressebeskyttelse(it) }
        return ConsumerRecord("topic", 0, 0L, "key", hendelse)
    }

    Given("filter") {
        When("gradering er STRENGT_FORTROLIG") { Then("filtreres ikke bort") { strategy.filter(record(STRENGT_FORTROLIG)) shouldBe false } }
        When("gradering er STRENGT_FORTROLIG_UTLAND") { Then("filtreres ikke bort") { strategy.filter(record(STRENGT_FORTROLIG_UTLAND)) shouldBe false } }
        When("gradering er FORTROLIG") { Then("filtreres ikke bort") { strategy.filter(record(FORTROLIG)) shouldBe false } }
        When("gradering er UGRADERT") { Then("filtreres bort") { strategy.filter(record(UGRADERT)).shouldBeTrue() } }
        When("hendelse mangler adressebeskyttelse") { Then("filtreres bort") { strategy.filter(record(null)).shouldBeTrue() } }
    }
})

