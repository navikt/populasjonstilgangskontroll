package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person

class BrukerTjenesteTest : BehaviorSpec({

    val pdl       = mockk<PdlTjeneste>()
    val skjerming = mockk<SkjermingTjeneste>()
    val brukerTjeneste = BrukerTjeneste(pdl, skjerming)

    val id1      = BrukerId("08526835670")
    val id2      = BrukerId("20478606614")
    val aktørId1 = AktørId("1111111111111")
    val aktørId2 = AktørId("2222222222222")
    val kommuneGT = KommuneTilknytning(Kommune("0301"))

    fun person(brukerId: BrukerId, aktørId: AktørId, gt: GeografiskTilknytning = kommuneGT) =
        Person(brukerId = brukerId, aktørId = aktørId, geoTilknytning = gt)

    beforeEach { clearMocks(pdl, skjerming) }

    Given("oppslag av flere brukere") {
        When("tom input") {
            Then("returneres tom mengde uten PDL-kall") {
                assertSoftly {
                    brukerTjeneste.brukere(emptySet()).shouldBeEmpty()
                    verify(exactly = 0) { pdl.personer(any()) }
                }
            }
        }
        When("bruker ikke er skjermet") {
            Then("returneres bruker uten SKJERMING-gruppe") {
                every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
                every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)
                val result = brukerTjeneste.brukere(setOf(id1.verdi)).single()
                assertSoftly {
                    result.brukerId shouldBe id1
                    result.påkrevdeGrupper shouldNotContain SKJERMING
                }
            }
        }
        When("bruker er skjermet") {
            Then("returneres bruker med SKJERMING-gruppe") {
                every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
                every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to true)
                (brukerTjeneste.brukere(setOf(id1.verdi)).single() kreverMedlemskapI SKJERMING).shouldBeTrue()
            }
        }
        When("to brukere returneres fra PDL") {
            Then("slås opp skjerming for alle og returneres begge") {
                every { pdl.personer(setOf(id1.verdi, id2.verdi)) } returns setOf(person(id1, aktørId1), person(id2, aktørId2))
                every { skjerming.skjerminger(any()) } returns mapOf(id1 to false, id2 to true)
                val result = brukerTjeneste.brukere(setOf(id1.verdi, id2.verdi))
                assertSoftly {
                    result shouldHaveSize 2
                    result.first { it.brukerId == id1 } kreverMedlemskapI SKJERMING shouldBe false
                    (result.first { it.brukerId == id2 } kreverMedlemskapI SKJERMING).shouldBeTrue()
                }
            }
        }
        When("PDL finner ingen personer") {
            Then("returneres tom mengde uten skjermingsoppslag") {
                every { pdl.personer(setOf(id1.verdi)) } returns emptySet()
                brukerTjeneste.brukere(setOf(id1.verdi)).shouldBeEmpty()
                verify(exactly = 0) { skjerming.skjerminger(any()) }
            }
        }
        When("PDL finner kun én av to") {
            Then("returneres kun den funne brukeren") {
                every { pdl.personer(setOf(id1.verdi, id2.verdi)) } returns setOf(person(id1, aktørId1))
                every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)
                brukerTjeneste.brukere(setOf(id1.verdi, id2.verdi)).single().brukerId shouldBe id1
            }
        }
        When("person har udefinert geografisk tilknytning") {
            Then("mapper geografisk tilknytning fra person") {
                val gt = UdefinertTilknytning()
                every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1, gt))
                every { skjerming.skjerminger(listOf(id1)) } returns mapOf(id1 to false)
                brukerTjeneste.brukere(setOf(id1.verdi)).single().geografiskTilknytning shouldBe gt
            }
        }
        When("brukerId mangler i skjermingsresultat") {
            Then("defaultes skjerming til false") {
                every { pdl.personer(setOf(id1.verdi)) } returns setOf(person(id1, aktørId1))
                every { skjerming.skjerminger(listOf(id1)) } returns emptyMap()
                brukerTjeneste.brukere(setOf(id1.verdi)).single() kreverMedlemskapI SKJERMING shouldBe false
            }
        }
    }

    Given("brukerMedNærmesteFamilie") {
        When("bruker er ikke skjermet") {
            Then("returneres bruker med riktig id") {
                every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns false
                brukerTjeneste.brukerMedNærmesteFamilie(id1.verdi).brukerId shouldBe id1
            }
        }
        When("bruker er skjermet") {
            Then("settes SKJERMING-gruppe") {
                every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns true
                (brukerTjeneste.brukerMedNærmesteFamilie(id1.verdi) kreverMedlemskapI SKJERMING).shouldBeTrue()
            }
        }
        When("brukerMedNærmesteFamilie kalles") {
            Then("kalles medFamilie, ikke medUtvidetFamilie") {
                every { pdl.medFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns false
                brukerTjeneste.brukerMedNærmesteFamilie(id1.verdi)
                assertSoftly {
                    verify { pdl.medFamilie(id1.verdi) }
                    verify(exactly = 0) { pdl.medUtvidetFamilie(any()) }
                }
            }
        }
    }

    Given("brukerMedUtvidetFamilie") {
        When("bruker er ikke skjermet") {
            Then("returneres bruker med riktig id") {
                every { pdl.medUtvidetFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns false
                brukerTjeneste.brukerMedUtvidetFamilie(id1.verdi).brukerId shouldBe id1
            }
        }
        When("bruker er skjermet") {
            Then("settes SKJERMING-gruppe") {
                every { pdl.medUtvidetFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns true
                (brukerTjeneste.brukerMedUtvidetFamilie(id1.verdi) kreverMedlemskapI SKJERMING).shouldBeTrue()
            }
        }
        When("brukerMedUtvidetFamilie kalles") {
            Then("kalles medUtvidetFamilie, ikke medFamilie") {
                every { pdl.medUtvidetFamilie(id1.verdi) } returns person(id1, aktørId1)
                every { skjerming.skjerming(id1) } returns false
                brukerTjeneste.brukerMedUtvidetFamilie(id1.verdi)
                assertSoftly {
                    verify { pdl.medUtvidetFamilie(id1.verdi) }
                    verify(exactly = 0) { pdl.medFamilie(any()) }
                }
            }
        }
    }
})
