package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN as BARN_REL
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.FAR as FAR_REL
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR as MOR_REL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BARN
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.FAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.MOR
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle

class PdlPersonMapperFamilierelasjonerTest : BehaviorSpec({
    Given("tilPerson - familierelasjoner") {
        fun familierelasjon(ident: BrukerId, rolle: PdlFamilieRelasjonRolle) = PdlFamilierelasjon(ident, rolle)

        When("MOR-relasjon") {
            Then("mappes til foreldre med relasjon MOR") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(MOR, PdlFamilieRelasjonRolle.MOR))))).foreldre.single().let {
                    it.brukerId shouldBe MOR
                    it.relasjon shouldBe MOR_REL
                }
            }
        }
        When("FAR-relasjon") {
            Then("mappes til foreldre med relasjon FAR") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(FAR, PdlFamilieRelasjonRolle.FAR))))).foreldre.single().let {
                    it.brukerId shouldBe FAR
                    it.relasjon shouldBe FAR_REL
                }
            }
        }
        When("MEDMOR-relasjon") {
            Then("mappes til foreldre med relasjon MOR") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(MOR, PdlFamilieRelasjonRolle.MEDMOR))))).foreldre.single().relasjon shouldBe MOR_REL
            }
        }
        When("MEDFAR-relasjon") {
            Then("mappes til foreldre med relasjon FAR") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(FAR, PdlFamilieRelasjonRolle.MEDFAR))))).foreldre.single().relasjon shouldBe FAR_REL
            }
        }
        When("BARN-relasjon") {
            Then("mappes til barn") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(BARN, PdlFamilieRelasjonRolle.BARN))))).barn.single().let {
                    it.brukerId shouldBe BARN
                    it.relasjon shouldBe BARN_REL
                }
            }
        }
        When("relasjon uten ident") {
            Then("mappes til ingenting") {
                val result = tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(null, PdlFamilieRelasjonRolle.BARN)))))
                assertSoftly {
                    result.barn.shouldBeEmpty()
                    result.foreldre.shouldBeEmpty()
                }
            }
        }
        When("null rolle med ident") {
            Then("kastes IllegalStateException") {
                shouldThrow<IllegalStateException> { tilPerson(BRUKER_ID, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(BARN, null))))) }
            }
        }
    }
})
