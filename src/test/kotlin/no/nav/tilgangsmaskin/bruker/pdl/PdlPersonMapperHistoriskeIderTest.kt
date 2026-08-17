package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.identer
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID

class PdlPersonMapperHistoriskeIderTest : BehaviorSpec({
    Given("tilPerson - historiske ids") {
        When("historisk FOLKEREGISTERIDENT") {
            Then("inkluderes") {
                tilPerson(BRUKER_ID, pdlRespons(identer = identer(historiske = listOf("12345678901" to FOLKEREGISTERIDENT)))).historiskeIds shouldContainExactly setOf(BrukerId("12345678901"))
            }
        }
        When("historisk NPID") {
            Then("inkluderes") {
                tilPerson(BRUKER_ID, pdlRespons(identer = identer(historiske = listOf("01234567890" to NPID)))).historiskeIds shouldContainExactly setOf(BrukerId("01234567890"))
            }
        }
        When("historisk AKTORID") {
            Then("ekskluderes") {
                tilPerson(BRUKER_ID, pdlRespons(identer = identer(historiske = listOf("9876543210123" to AKTORID)))).historiskeIds.shouldBeEmpty()
            }
        }
        When("ingen historiske identer") {
            Then("er tom") {
                tilPerson(BRUKER_ID, pdlRespons()).historiskeIds.shouldBeEmpty()
            }
        }
    }
})
