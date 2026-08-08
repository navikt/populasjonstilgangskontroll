package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering

class PdlPersonMapperGraderingerTest : BehaviorSpec({
    Given("tilPerson - graderinger") {
        When("ingen adressebeskyttelse") {
            Then("graderingsliste er tom") {
                tilPerson(BRUKER_ID, pdlRespons()).graderinger.shouldBeEmpty()
            }
        }
        When("STRENGT_FORTROLIG_UTLAND") {
            Then("mappes korrekt") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG_UTLAND)
            }
        }
        When("STRENGT_FORTROLIG") {
            Then("mappes korrekt") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG)
            }
        }
        When("FORTROLIG") {
            Then("mappes korrekt") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.FORTROLIG)
            }
        }
        When("UGRADERT") {
            Then("mappes korrekt") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.UGRADERT))))).graderinger shouldContainExactly listOf(Gradering.UGRADERT)
            }
        }
    }
})
