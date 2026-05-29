package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTBydel
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilGeoTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlDødsfall
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import java.time.LocalDate

class PdlPersonMapperTest : BehaviorSpec({

    val brukerId = "08526835670"
    val aktorId  = "1234567890123"
    val barn = BrukerId("01010112345")
    val mor  = BrukerId("01010198765")
    val far  = BrukerId("01010154321")

    fun identer(fnr: String = brukerId, aktor: String = aktorId, historiske: List<Pair<String, PdlIdentGruppe>> = emptyList()) =
        PdlIdenter(buildList {
            add(PdlIdent(fnr, false, FOLKEREGISTERIDENT))
            add(PdlIdent(aktor, false, AKTORID))
            historiske.forEach { (ident, gruppe) -> add(PdlIdent(ident, true, gruppe)) }
        })

    fun pdlRespons(person: PdlPerson = PdlPerson(), geo: PdlGeografiskTilknytning? = PdlGeografiskTilknytning(UDEFINERT), identer: PdlIdenter = identer()) =
        PdlPipRespons(person, identer, geo)

    Given("tilGeoTilknytning") {
        When("input er null") { Then("mappes til UdefinertTilknytning") { tilGeoTilknytning(null).shouldBeInstanceOf<UdefinertTilknytning>() } }
        When("UDEFINERT") { Then("mappes til UdefinertTilknytning") { tilGeoTilknytning(PdlGeografiskTilknytning(UDEFINERT)).shouldBeInstanceOf<UdefinertTilknytning>() } }
        When("UTLAND med land") { Then("mappes til UtenlandskTilknytning") { tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))).shouldBeInstanceOf<UtenlandskTilknytning>() } }
        When("UTLAND uten land") { Then("mappes til UkjentBosted") { tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND)).shouldBeInstanceOf<UkjentBosted>() } }
        When("KOMMUNE med kode") {
            Then("mappes til KommuneTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")))
                    .shouldBeInstanceOf<KommuneTilknytning>().kommune.verdi shouldBe "0301"
            }
        }
        When("KOMMUNE uten kode") { Then("mappes til UkjentBosted") { tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE)).shouldBeInstanceOf<UkjentBosted>() } }
        When("BYDEL med kode") {
            Then("mappes til BydelTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("030101")))
                    .shouldBeInstanceOf<BydelTilknytning>().bydel.verdi shouldBe "030101"
            }
        }
        When("BYDEL uten kode") { Then("mappes til UkjentBosted") { tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL)).shouldBeInstanceOf<UkjentBosted>() } }
    }

    Given("tilPerson - familierelasjoner") {
        fun familierelasjon(ident: BrukerId, rolle: PdlFamilieRelasjonRolle) = PdlFamilierelasjon(ident, rolle)

        When("MOR-relasjon") {
            Then("mappes til foreldre") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MOR))))).foreldre.single() shouldBe mor
            }
        }
        When("FAR-relasjon") {
            Then("mappes til foreldre") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.FAR))))).foreldre.single() shouldBe far
            }
        }
        When("MEDMOR-relasjon") { Then("mappes til foreldre") { tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MEDMOR))))).foreldre.single() shouldBe mor } }
        When("MEDFAR-relasjon") { Then("mappes til foreldre") { tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.MEDFAR))))).foreldre.single() shouldBe far } }
        When("BARN-relasjon") {
            Then("mappes til barn") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(barn, PdlFamilieRelasjonRolle.BARN))))).barn.single() shouldBe barn
            }
        }
        When("relasjon uten ident") {
            Then("mappes til ingenting") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(null, PdlFamilieRelasjonRolle.BARN)))))
                assertSoftly { result.barn.shouldBeEmpty(); result.foreldre.shouldBeEmpty() }
            }
        }
    }

    Given("tilPerson - graderinger") {
        When("ingen adressebeskyttelse") { Then("graderingsliste er tom") { tilPerson(brukerId, pdlRespons()).graderinger.shouldBeEmpty() } }
        When("STRENGT_FORTROLIG_UTLAND") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG_UTLAND) } }
        When("STRENGT_FORTROLIG") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG) } }
        When("FORTROLIG") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.FORTROLIG) } }
        When("UGRADERT") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.UGRADERT))))).graderinger shouldContainExactly listOf(Gradering.UGRADERT) } }
    }

    Given("tilPerson - dødsdato") {
        When("ingen dødsfall") { Then("returneres null") { tilPerson(brukerId, pdlRespons()).dødsdato.shouldBeNull() } }
        When("ett dødsfall") {
            Then("returneres dødsdato") {
                val dato = LocalDate.of(2024, 1, 15)
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(dato))))).dødsdato shouldBe dato
            }
        }
        When("flere dødsfall") {
            Then("returneres seneste dødsdato") {
                val tidlig = LocalDate.of(2023, 1, 1)
                val sen = LocalDate.of(2024, 6, 1)
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(tidlig), PdlDødsfall(sen))))).dødsdato shouldBe sen
            }
        }
        When("dødsfall uten dato") {
            Then("returneres null") {
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall())))).dødsdato.shouldBeNull()
            }
        }
        When("flere dødsfall der noen mangler dato") {
            Then("returneres seneste kjente dødsdato") {
                val dato = LocalDate.of(2024, 3, 1)
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(), PdlDødsfall(dato))))).dødsdato shouldBe dato
            }
        }
    }

    Given("tilPerson - historiske ids") {
        When("historisk FOLKEREGISTERIDENT") { Then("inkluderes") { tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf("12345678901" to FOLKEREGISTERIDENT)))).historiskeIds shouldContainExactly setOf(BrukerId("12345678901")) } }
        When("historisk NPID") { Then("inkluderes") { tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf("01234567890" to NPID)))).historiskeIds shouldContainExactly setOf(BrukerId("01234567890")) } }
        When("historisk AKTORID") { Then("ekskluderes") { tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf("9876543210123" to AKTORID)))).historiskeIds.shouldBeEmpty() } }
        When("ingen historiske identer") { Then("er tom") { tilPerson(brukerId, pdlRespons()).historiskeIds.shouldBeEmpty() } }
    }

    Given("tilPersoner") {
        When("flere responser") {
            Then("mappes til map keyed på oppslagId") {
                val brukerId2 = "20478606614"
                val aktorId2  = "9876543210987"
                val result = tilPersoner(mapOf(
                    brukerId  to pdlRespons(),
                    brukerId2 to pdlRespons(identer = identer(fnr = brukerId2, aktor = aktorId2), geo = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")))
                ))
                assertSoftly(result) {
                    shouldHaveSize(2)
                    get(brukerId).shouldNotBeNull().brukerId shouldBe BrukerId(brukerId)
                    get(brukerId2).shouldNotBeNull().geoTilknytning.shouldBeInstanceOf<KommuneTilknytning>()
                }
            }
        }
        When("responser med null") { Then("filtreres null ut") { val r = tilPersoner(mapOf(brukerId to pdlRespons(), "ukjent" to null)); assertSoftly(r) { shouldHaveSize(1); get(brukerId).shouldNotBeNull() } } }
        When("ingen responser") { Then("returneres tom map") { tilPersoner(emptyMap()).shouldBeEmpty() } }
    }

    Given("tilPerson - identifikasjon") {
        When("FOLKEREGISTERIDENT mangler, men NPID finnes") {
            Then("brukes NPID som brukerId") {
                val npid = "01234567890"
                tilPerson(brukerId, pdlRespons(identer = PdlIdenter(listOf(PdlIdent(npid, false, NPID), PdlIdent(aktorId, false, AKTORID))))).brukerId shouldBe BrukerId(npid)
            }
        }
        When("aktørId mangler") { Then("kastes IllegalStateException") { shouldThrow<IllegalStateException> { PdlPipRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(brukerId, false, FOLKEREGISTERIDENT)))) } } }
        When("brukerId mangler") { Then("kastes IllegalStateException") { shouldThrow<IllegalStateException> { PdlPipRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(aktorId, false, AKTORID)))) } } }
    }
})
