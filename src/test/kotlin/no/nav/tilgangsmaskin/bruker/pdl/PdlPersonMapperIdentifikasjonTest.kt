package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.AKTOR_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson

class PdlPersonMapperIdentifikasjonTest : BehaviorSpec({
    Given("tilPerson - identifikasjon") {
        When("FOLKEREGISTERIDENT mangler, men NPID finnes") {
            Then("brukes NPID som brukerId") {
                val npid = "01234567890"
                tilPerson(
                    BRUKER_ID,
                    pdlRespons(identer = PdlIdenter(listOf(PdlIdent(npid, false, NPID), PdlIdent(AKTOR_ID, false, AKTORID))))
                ).brukerId shouldBe BrukerId(npid)
            }
        }
        When("aktørId mangler") {
            Then("kastes IllegalStateException") {
                shouldThrow<IllegalStateException> { PdlPipRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(BRUKER_ID, false, FOLKEREGISTERIDENT)))) }
            }
        }
        When("brukerId mangler") {
            Then("kastes IllegalStateException") {
                shouldThrow<IllegalStateException> { PdlPipRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(AKTOR_ID, false, AKTORID)))) }
            }
        }
    }
})
