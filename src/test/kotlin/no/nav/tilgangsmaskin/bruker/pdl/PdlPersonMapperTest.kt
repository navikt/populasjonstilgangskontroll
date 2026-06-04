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
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.FAR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.ENKE_ELLER_ENKEMANN
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GIFT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GJENLEVENDE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.REGISTRERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.UGIFT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.UOPPGITT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTBydel
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilGeoTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
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
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.UGRADERT
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

    Given("tilPartner") {

        When("GIFT") { Then("mappes til PARTNER") { tilPartner(GIFT) shouldBe PARTNER } }

        When("REGISTRERT_PARTNER") { Then("mappes til PARTNER") { tilPartner(REGISTRERT_PARTNER) shouldBe PARTNER } }

        When("SKILT") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SKILT) shouldBe TIDLIGERE_PARTNER } }

        When("ENKE_ELLER_ENKEMANN") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(ENKE_ELLER_ENKEMANN) shouldBe TIDLIGERE_PARTNER } }

        When("SEPARERT") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SEPARERT) shouldBe TIDLIGERE_PARTNER } }

        When("SKILT_PARTNER") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SKILT_PARTNER) shouldBe TIDLIGERE_PARTNER } }

        When("GJENLEVENDE_PARTNER") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(GJENLEVENDE_PARTNER) shouldBe TIDLIGERE_PARTNER } }

        When("SEPARERT_PARTNER") { Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SEPARERT_PARTNER) shouldBe TIDLIGERE_PARTNER } }

        When("UGIFT") { Then("mappes til INGEN") { tilPartner(UGIFT) shouldBe INGEN } }

        When("UOPPGITT") { Then("mappes til INGEN") { tilPartner(UOPPGITT) shouldBe INGEN } }
    }

    Given("tilPerson - familierelasjoner") {
        fun familierelasjon(ident: BrukerId, rolle: PdlFamilieRelasjonRolle) = PdlFamilierelasjon(ident, rolle)

        When("MOR-relasjon") {
            Then("mappes til foreldre med relasjon MOR") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MOR))))).foreldre.single().let {
                    it.brukerId shouldBe mor; it.relasjon shouldBe MOR
                }
            }
        }
        When("FAR-relasjon") {
            Then("mappes til foreldre med relasjon FAR") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.FAR))))).foreldre.single().let {
                    it.brukerId shouldBe far; it.relasjon shouldBe FAR
                }
            }
        }

        When("MEDMOR-relasjon") { Then("mappes til foreldre med relasjon MOR") { tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MEDMOR))))).foreldre.single().relasjon shouldBe MOR } }

        When("MEDFAR-relasjon") { Then("mappes til foreldre med relasjon FAR") { tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.MEDFAR))))).foreldre.single().relasjon shouldBe FAR } }

        When("BARN-relasjon") {
            Then("mappes til barn") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(barn, PdlFamilieRelasjonRolle.BARN))))).barn.single().let {
                    it.brukerId shouldBe barn; it.relasjon shouldBe BARN
                }
            }
        }

        When("relasjon uten ident") {
            Then("mappes til ingenting") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(null, PdlFamilieRelasjonRolle.BARN)))))
                assertSoftly { result.barn.shouldBeEmpty(); result.foreldre.shouldBeEmpty() }
            }
        }

        When("null rolle med ident") {
            Then("kastes IllegalStateException") {
                shouldThrow<IllegalStateException> { tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(barn, null))))) }
            }
        }

        When("kombinert familie med MOR, FAR og BARN") {
            Then("mappes til korrekt foreldre og barn") {
                val person = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(
                    familierelasjon(mor, PdlFamilieRelasjonRolle.MOR),
                    familierelasjon(far, PdlFamilieRelasjonRolle.FAR),
                    familierelasjon(barn, PdlFamilieRelasjonRolle.BARN)
                ))))
                assertSoftly {
                    person.familie.medlemmer.size shouldBe 3
                    person.foreldre.size shouldBe 2
                    person.barn.size shouldBe 1
                    person.familie.søsken.shouldBeEmpty()
                    person.familie.partnere.shouldBeEmpty()
                }
            }
        }
    }

    Given("tilPerson - graderinger") {
        When("ingen adressebeskyttelse") { Then("graderingsliste er tom") { tilPerson(brukerId, pdlRespons()).graderinger.shouldBeEmpty() } }

        When("STRENGT_FORTROLIG_UTLAND") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(
            STRENGT_FORTROLIG_UTLAND))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG_UTLAND) } }

        When("STRENGT_FORTROLIG") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(
            STRENGT_FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG) } }

        When("FORTROLIG") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(
            FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.FORTROLIG) } }

        When("UGRADERT") { Then("mappes korrekt") { tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(
            UGRADERT))))).graderinger shouldContainExactly listOf(Gradering.UGRADERT) } }

        When("flere graderinger") {
            Then("alle mappes") {
                tilPerson(brukerId, pdlRespons(PdlPerson(listOf(
                    PdlAdressebeskyttelse(FORTROLIG),
                    PdlAdressebeskyttelse(UGRADERT)
                )))).graderinger shouldContainExactly listOf(Gradering.FORTROLIG, Gradering.UGRADERT)
            }
        }
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
        When("oppslagId settes korrekt") {
            Then("oppslagId er det som ble gitt inn") {
                val customOppslagId = "custom-oppslag-id"
                tilPerson(customOppslagId, pdlRespons()).oppslagId shouldBe customOppslagId
            }
        }

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
