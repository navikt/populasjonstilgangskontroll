package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
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

class PdlPersonMapperTest : DescribeSpec({

    val brukerId = "08526835670"
    val aktorId = "1234567890123"
    val barn = BrukerId("01010112345")
    val mor = BrukerId("01010198765")
    val far = BrukerId("01010154321")

    fun identer(fnr: String = brukerId, aktor: String = aktorId, historiske: List<Pair<String, PdlIdentGruppe>> = emptyList()) =
        PdlIdenter(buildList {
            add(PdlIdent(fnr, false, FOLKEREGISTERIDENT))
            add(PdlIdent(aktor, false, AKTORID))
            historiske.forEach {
                    (ident, gruppe) -> add(PdlIdent(ident, true, gruppe))
            }
        })

    fun pdlRespons(person: PdlPerson = PdlPerson(), geo: PdlGeografiskTilknytning? = PdlGeografiskTilknytning(UDEFINERT), identer: PdlIdenter = identer()) =
        PdlRespons(person, identer, geo)

    describe("tilGeoTilknytning") {

        it("mapper null til UdefinertTilknytning") {
            tilGeoTilknytning(null).shouldBeInstanceOf<UdefinertTilknytning>()
        }

        it("mapper UDEFINERT til UdefinertTilknytning") {
            tilGeoTilknytning(PdlGeografiskTilknytning(UDEFINERT)).shouldBeInstanceOf<UdefinertTilknytning>()
        }

        it("mapper UTLAND med land til UtenlandskTilknytning") {
            tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))).shouldBeInstanceOf<UtenlandskTilknytning>()
        }

        it("mapper UTLAND uten land til UkjentBosted") {
            tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND)).shouldBeInstanceOf<UkjentBosted>()
        }

        it("mapper KOMMUNE med kode til KommuneTilknytning med riktig verdi") {
            val result = tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")))
            result.shouldBeInstanceOf<KommuneTilknytning>()
            result.kommune.verdi shouldBe "0301"
        }

        it("mapper KOMMUNE uten kode til UkjentBosted") {
            tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE)).shouldBeInstanceOf<UkjentBosted>()
        }

        it("mapper BYDEL med kode til BydelTilknytning med riktig verdi") {
            val result = tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("030101")))
            result.shouldBeInstanceOf<BydelTilknytning>()
            result.bydel.verdi shouldBe "030101"
        }

        it("mapper BYDEL uten kode til UkjentBosted") {
            tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL)).shouldBeInstanceOf<UkjentBosted>()
        }
    }

    describe("tilPartner") {

        it("mapper GIFT til PARTNER") { tilPartner(GIFT) shouldBe PARTNER }
        it("mapper REGISTRERT_PARTNER til PARTNER") { tilPartner(REGISTRERT_PARTNER) shouldBe PARTNER }
        it("mapper SKILT til TIDLIGERE_PARTNER") { tilPartner(SKILT) shouldBe TIDLIGERE_PARTNER }
        it("mapper ENKE_ELLER_ENKEMANN til TIDLIGERE_PARTNER") { tilPartner(ENKE_ELLER_ENKEMANN) shouldBe TIDLIGERE_PARTNER }
        it("mapper SEPARERT til TIDLIGERE_PARTNER") { tilPartner(SEPARERT) shouldBe TIDLIGERE_PARTNER }
        it("mapper SKILT_PARTNER til TIDLIGERE_PARTNER") { tilPartner(SKILT_PARTNER) shouldBe TIDLIGERE_PARTNER }
        it("mapper GJENLEVENDE_PARTNER til TIDLIGERE_PARTNER") { tilPartner(GJENLEVENDE_PARTNER) shouldBe TIDLIGERE_PARTNER }
        it("mapper SEPARERT_PARTNER til TIDLIGERE_PARTNER") { tilPartner(SEPARERT_PARTNER) shouldBe TIDLIGERE_PARTNER }
        it("mapper UGIFT til INGEN") { tilPartner(UGIFT) shouldBe INGEN }
        it("mapper UOPPGITT til INGEN") { tilPartner(UOPPGITT) shouldBe INGEN }
    }

    describe("tilPerson - familierelasjoner") {

        fun familierelasjon(ident: BrukerId, rolle: PdlFamilieRelasjonRolle) =
            PdlFamilierelasjon(ident, rolle)

        it("mapper MOR-relasjon til foreldre med relasjon MOR") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MOR)))))
            result.foreldre.single().let {
                it.brukerId shouldBe mor
                it.relasjon shouldBe MOR
            }
        }

        it("mapper FAR-relasjon til foreldre med relasjon FAR") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.FAR)))))
            result.foreldre.single().let {
                it.brukerId shouldBe far
                it.relasjon shouldBe FAR
            }
        }

        it("mapper MEDMOR-relasjon til foreldre med relasjon MOR") {
            tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(mor, PdlFamilieRelasjonRolle.MEDMOR))))).foreldre.single().relasjon shouldBe MOR
        }

        it("mapper MEDFAR-relasjon til foreldre med relasjon FAR") {
            tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(far, PdlFamilieRelasjonRolle.MEDFAR))))).foreldre.single().relasjon shouldBe FAR
        }

        it("mapper BARN-relasjon til barn") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(familierelasjon(barn, PdlFamilieRelasjonRolle.BARN)))))
            result.barn.single().let {
                it.brukerId shouldBe barn
                it.relasjon shouldBe BARN
            }
        }

        it("mapper relasjon uten ident til ingenting") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(null, PdlFamilieRelasjonRolle.BARN)))))
            result.barn.shouldBeEmpty()
            result.foreldre.shouldBeEmpty()
        }

        it("kaster exception for ukjent relasjon (null rolle med ident)") {
            shouldThrow<IllegalStateException> {
                tilPerson(brukerId, pdlRespons(PdlPerson(familierelasjoner = listOf(PdlFamilierelasjon(barn, null)))))
            }
        }
    }

    describe("tilPerson - graderinger") {

        it("mapper ingen adressebeskyttelse til tom graderingsliste") {
            tilPerson(brukerId, pdlRespons()).graderinger.shouldBeEmpty()
        }

        it("mapper STRENGT_FORTROLIG_UTLAND") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND)))))
            result.graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG_UTLAND)
        }

        it("mapper STRENGT_FORTROLIG") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)))))
            result.graderinger shouldContainExactly listOf(Gradering.STRENGT_FORTROLIG)
        }

        it("mapper FORTROLIG") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.FORTROLIG)))))
            result.graderinger shouldContainExactly listOf(Gradering.FORTROLIG)
        }

        it("mapper UGRADERT") {
            val result = tilPerson(brukerId, pdlRespons(PdlPerson(listOf(PdlAdressebeskyttelse(PdlAdressebeskyttelseGradering.UGRADERT)))))
            result.graderinger shouldContainExactly listOf(Gradering.UGRADERT)
        }
    }

    describe("tilPerson - dødsdato") {

        it("returnerer null når det ikke finnes dødsfall") {
            tilPerson(brukerId, pdlRespons()).dødsdato.shouldBeNull()
        }

        it("returnerer dødsdato fra dødsfall") {
            val dato = LocalDate.of(2024, 1, 15)
            tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(dato))))).dødsdato shouldBe dato
        }

        it("returnerer seneste dødsdato ved flere dødsfall") {
            val tidlig = LocalDate.of(2023, 1, 1)
            val sen = LocalDate.of(2024, 6, 1)
            tilPerson(brukerId, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(tidlig), PdlDødsfall(sen))))).dødsdato shouldBe sen
        }
    }

    describe("tilPerson - historiske ids") {

        it("inkluderer historiske FOLKEREGISTERIDENT") {
            val historiskId = "12345678901"
            tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskId to FOLKEREGISTERIDENT)))).historiskeIds shouldContainExactly setOf(BrukerId(historiskId))
        }

        it("inkluderer historiske NPID") {
            val historiskNpid = "01234567890"
            tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskNpid to NPID)))).historiskeIds shouldContainExactly setOf(BrukerId(historiskNpid))
        }

        it("ekskluderer historiske AKTORID") {
            val historiskAktorId = "9876543210123"
            tilPerson(brukerId, pdlRespons(identer = identer(historiske = listOf(historiskAktorId to AKTORID)))).historiskeIds.shouldBeEmpty()
        }

        it("er tom når det ikke finnes historiske identer") {
            tilPerson(brukerId, pdlRespons()).historiskeIds.shouldBeEmpty()
        }
    }

    describe("tilPersoner") {

        it("mapper flere responser til persons map keyed på oppslagId") {
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
            result shouldHaveSize 2
            result[brukerId].shouldNotBeNull().brukerId shouldBe BrukerId(brukerId)
            result[brukerId2].shouldNotBeNull().geoTilknytning.shouldBeInstanceOf<KommuneTilknytning>()
        }

        it("filtrerer ut null-responser") {
            val result = tilPersoner(mapOf(brukerId to pdlRespons(), "ukjent" to null))
            result shouldHaveSize 1
            result[brukerId].shouldNotBeNull()
        }

        it("returnerer tom map ved ingen responser") {
            tilPersoner(emptyMap()).shouldBeEmpty()
        }
    }

    describe("tilPerson - identifikasjon") {

        it("bruker NPID som brukerId når FOLKEREGISTERIDENT mangler") {
            val npid = "01234567890"
            val identerMedNpid = PdlIdenter(listOf(
                PdlIdent(npid, false, NPID),
                PdlIdent(aktorId, false, AKTORID),
            ))
            tilPerson(brukerId, pdlRespons(identer = identerMedNpid)).brukerId shouldBe BrukerId(npid)
        }

        it("kaster exception når aktørId mangler") {
            shouldThrow<IllegalStateException> {
                PdlRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(brukerId, false, FOLKEREGISTERIDENT))))
            }
        }

        it("kaster exception når brukerId mangler") {
            shouldThrow<IllegalStateException> {
                PdlRespons(PdlPerson(), PdlIdenter(listOf(PdlIdent(aktorId, false, AKTORID))))
            }
        }
    }
})
