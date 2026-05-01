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
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlDødsfall
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import java.time.LocalDate

class PdlPersonMapperTest : BehaviorSpec({

    val brukerId = "08526835670"
    val aktorId = "1234567890123"
    val barn = BrukerId("01010112345")
    val mor = BrukerId("01010198765")
    val far = BrukerId("01010154321")

    fun identer(fnr: String = brukerId, aktor: String = aktorId, historiske: List<Pair<String, PdlIdentGruppe>> = emptyList()) =
        PdlIdenter(buildSet {
            add(PdlIdent(fnr, false, FOLKEREGISTERIDENT))
            add(PdlIdent(aktor, false, AKTORID))
            historiske.forEach { (ident, gruppe) -> add(PdlIdent(ident, true, gruppe)) }
        })

    fun pdlRespons(person: PdlPerson = PdlPerson(), geo: PdlGeografiskTilknytning? = PdlGeografiskTilknytning(UDEFINERT), identer: PdlIdenter = identer()) =
        PdlRespons(person, identer, geo)

    Given("tilGeoTilknytning") {
        When("input er null") {
            Then("mapper til UdefinertTilknytning") {
                tilGeoTilknytning(null).shouldBeInstanceOf<UdefinertTilknytning>()
            }
        }
        When("input er UDEFINERT") {
            Then("mapper til UdefinertTilknytning") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UDEFINERT)).shouldBeInstanceOf<UdefinertTilknytning>()
            }
        }
        When("input er UTLAND med land") {
            Then("mapper til UtenlandskTilknytning") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))).shouldBeInstanceOf<UtenlandskTilknytning>()
            }
        }
        When("input er UTLAND uten land") {
            Then("mapper til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("input er KOMMUNE med kode") {
            Then("mapper til KommuneTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")))
                    .shouldBeInstanceOf<KommuneTilknytning>()
                    .kommune.verdi shouldBe "0301"
            }
        }
        When("input er KOMMUNE uten kode") {
            Then("mapper til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("input er BYDEL med kode") {
            Then("mapper til BydelTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("030101")))
                    .shouldBeInstanceOf<BydelTilknytning>()
                    .bydel.verdi shouldBe "030101"
            }
        }
        When("input er BYDEL uten kode") {
            Then("mapper til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
    }

    Given("tilPartner") {
        When("sivilstandstype er GIFT") { Then("mapper til PARTNER") { tilPartner(GIFT) shouldBe PARTNER } }
        When("sivilstandstype er REGISTRERT_PARTNER") { Then("mapper til PARTNER") { tilPartner(REGISTRERT_PARTNER) shouldBe PARTNER } }
        When("sivilstandstype er SKILT") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(SKILT) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er ENKE_ELLER_ENKEMANN") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(ENKE_ELLER_ENKEMANN) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er SEPARERT") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(SEPARERT) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er SKILT_PARTNER") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(SKILT_PARTNER) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er GJENLEVENDE_PARTNER") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(GJENLEVENDE_PARTNER) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er SEPARERT_PARTNER") { Then("mapper til TIDLIGERE_PARTNER") { tilPartner(SEPARERT_PARTNER) shouldBe TIDLIGERE_PARTNER } }
        When("sivilstandstype er UGIFT") { Then("mapper til INGEN") { tilPartner(UGIFT) shouldBe INGEN } }
        When("sivilstandstype er UOPPGITT") { Then("mapper til INGEN") { tilPartner(UOPPGITT) shouldBe INGEN } }
    }

    Given("tilPerson - familierelasjoner") {

        fun familierelasjon(ident: BrukerId, rolle: PdlFamilieRelasjonRolle) = PdlFamilierelasjon(ident, rolle)

        When("relasjon er MOR") {
            Then("mapper til foreldre med relasjon MOR") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MOR)))))
                result.foreldre.single().let {
                    it.brukerId shouldBe mor
                    it.relasjon shouldBe MOR
                }
            }
        }
        When("relasjon er FAR") {
            Then("mapper til foreldre med relasjon FAR") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(familierelasjon(far, PdlFamilieRelasjonRolle.FAR)))))
                result.foreldre.single().let {
                    it.brukerId shouldBe far
                    it.relasjon shouldBe FAR
                }
            }
        }
        When("relasjon er MEDMOR") {
            Then("mapper til foreldre med relasjon MOR") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MEDMOR))))).foreldre.single().relasjon shouldBe MOR
            }
        }
        When("relasjon er MEDFAR") {
            Then("mapper til foreldre med relasjon FAR") {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(familierelasjon(far, PdlFamilieRelasjonRolle.MEDFAR))))).foreldre.single().relasjon shouldBe FAR
            }
        }
        When("relasjon er BARN") {
            Then("mapper til barn") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(familierelasjon(barn, PdlFamilieRelasjonRolle.BARN)))))
                result.barn.single().let {
                    it.brukerId shouldBe barn
                    it.relasjon shouldBe BARN
                }
            }
        }
        When("relasjon mangler ident") {
            Then("mapper til ingen barn eller foreldre") {
                val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(PdlFamilierelasjon(null, PdlFamilieRelasjonRolle.BARN)))))
                assertSoftly {
                    result.barn.shouldBeEmpty()
                    result.foreldre.shouldBeEmpty()
                }
            }
        }
        When("relasjon har ident men null rolle") {
            Then("kaster exception") {
                shouldThrow<IllegalStateException> {
                    tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = setOf(PdlFamilierelasjon(barn, null)))))
                }
            }
        }
    }

    Given("tilPerson - graderinger") {
        When("det ikke finnes adressebeskyttelse") {
            Then("er graderingslisten tom") {
                tilPerson(brukerId, pdlRespons()).graderinger.shouldBeEmpty()
            }
        }
        When("gradering er STRENGT_FORTROLIG_UTLAND") {
            Then("mapper korrekt") {
                tilPerson(brukerId, pdlRespons(PdlPerson(setOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG_UTLAND)
            }
        }
        When("gradering er STRENGT_FORTROLIG") {
            Then("mapper korrekt") {
                tilPerson(brukerId, pdlRespons(PdlPerson(setOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG)
            }
        }
        When("gradering er FORTROLIG") {
            Then("mapper korrekt") {
                tilPerson(brukerId, pdlRespons(PdlPerson(setOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.FORTROLIG))))).graderinger shouldContainExactly listOf(Gradering.FORTROLIG)
            }
        }
        When("gradering er UGRADERT") {
            Then("mapper korrekt") {
                tilPerson(brukerId, pdlRespons(PdlPerson(setOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.UGRADERT))))).graderinger shouldContainExactly listOf(Gradering.UGRADERT)
            }
        }
    }

    Given("tilPerson - dødsdato") {
        When("det ikke finnes dødsfall") {
            Then("returneres null") {
                tilPerson(brukerId, pdlRespons()).dødsdato.shouldBeNull()
            }
        }
        When("det finnes ett dødsfall") {
            Then("returneres dødsdatoen") {
                val dato = LocalDate.of(2024, 1, 15)
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = setOf(PdlDødsfall(dato))))).dødsdato shouldBe dato
            }
        }
        When("det finnes flere dødsfall") {
            Then("returneres den seneste dødsdatoen") {
                val tidlig = LocalDate.of(2023, 1, 1)
                val sen = LocalDate.of(2024, 6, 1)
                tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = setOf(PdlDødsfall(tidlig), PdlDødsfall(sen))))).dødsdato shouldBe sen
            }
        }
    }

    Given("tilPerson - historiske ids") {
        When("det finnes historisk FOLKEREGISTERIDENT") {
            Then("inkluderes den i historiskeIds") {
                val historiskId = "12345678901"
                tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskId to FOLKEREGISTERIDENT)))).historiskeIds shouldContainExactly setOf(BrukerId(historiskId))
            }
        }
        When("det finnes historisk NPID") {
            Then("inkluderes den i historiskeIds") {
                val historiskNpid = "01234567890"
                tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskNpid to NPID)))).historiskeIds shouldContainExactly setOf(BrukerId(historiskNpid))
            }
        }
        When("det finnes historisk AKTORID") {
            Then("ekskluderes den fra historiskeIds") {
                val historiskAktorId = "9876543210123"
                tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskAktorId to AKTORID)))).historiskeIds.shouldBeEmpty()
            }
        }
        When("det ikke finnes historiske identer") {
            Then("er historiskeIds tom") {
                tilPerson(brukerId, pdlRespons()).historiskeIds.shouldBeEmpty()
            }
        }
    }

    Given("tilPersoner") {
        When("responser inneholder flere brukere") {
            Then("mapper til persons map keyed på oppslagId") {
                val brukerId2 = "20478606614"
                val aktorId2 = "9876543210987"
                val responser = mapOf(
                    brukerId to pdlRespons(),
                    brukerId2 to pdlRespons(
                        identer = identer(fnr = brukerId2, aktor = aktorId2),
                        geo = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")),
                    )
                )
                val result = tilPersoner(responser)
                assertSoftly(result) {
                    shouldHaveSize(2)
                    get(brukerId).shouldNotBeNull().brukerId shouldBe BrukerId(brukerId)
                    get(brukerId2).shouldNotBeNull().geoTilknytning.shouldBeInstanceOf<KommuneTilknytning>()
                }
            }
        }
        When("responser inneholder null-verdier") {
            Then("filtreres null-responser ut") {
                val result = tilPersoner(mapOf(brukerId to pdlRespons(), "ukjent" to null))
                assertSoftly(result) {
                    shouldHaveSize(1)
                    get(brukerId).shouldNotBeNull()
                }
            }
        }
        When("responser er tomme") {
            Then("returneres tom map") {
                tilPersoner(emptyMap()).shouldBeEmpty()
            }
        }
    }

    Given("tilPerson - identifikasjon") {
        When("FOLKEREGISTERIDENT mangler og NPID finnes") {
            Then("brukes NPID som brukerId") {
                val npid = "01234567890"
                val identerMedNpid = PdlIdenter(setOf(
                    PdlIdent(npid, false, NPID),
                    PdlIdent(aktorId, false, AKTORID),
                ))
                tilPerson(brukerId, pdlRespons(identer = identerMedNpid)).brukerId shouldBe BrukerId(npid)
            }
        }
        When("aktørId mangler") {
            Then("kaster exception") {
                shouldThrow<IllegalStateException> {
                    PdlRespons(PdlPerson(), PdlIdenter(setOf(PdlIdent(brukerId, false, FOLKEREGISTERIDENT))))
                }
            }
        }
        When("brukerId mangler") {
            Then("kaster exception") {
                shouldThrow<IllegalStateException> {
                    PdlRespons(PdlPerson(), PdlIdenter(setOf(PdlIdent(aktorId, false, AKTORID))))
                }
            }
        }
    }
})
