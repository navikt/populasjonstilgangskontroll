package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class BrukerMapperTest : DescribeSpec({

    val brukerId = BrukerBuilder(BrukerId("08526835670")).build().brukerId.verdi
    val aktorId = "1234567890123"

    fun geoUtland() = PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))
    fun geoKommune() = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))

    fun pipRespons(
        gradering: PdlAdressebeskyttelseGradering? = null,
        geo: PdlGeografiskTilknytning = geoUtland(),
    ) = PdlRespons(
        PdlPerson(gradering?.let { listOf(PdlAdressebeskyttelse(it)) } ?: emptyList()),
        PdlIdenter(listOf(PdlIdent(brukerId, false, FOLKEREGISTERIDENT), PdlIdent(aktorId, false, AKTORID))),
        geo
    )

    describe("tilBruker") {

        it("STRENGT_FORTROLIG_UTLAND krever STRENGT_FORTROLIG_UTLAND-gruppe og gir UtenlandskTilknytning") {
            val bruker = tilBruker(tilPerson(brukerId, pipRespons(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND)), false)
            assertSoftly(bruker) {
                påkrevdeGrupper shouldContainExactly setOf(STRENGT_FORTROLIG_UTLAND)
                geografiskTilknytning.shouldBeInstanceOf<UtenlandskTilknytning>()
            }
        }

        it("STRENGT_FORTROLIG med kommunal geo krever STRENGT_FORTROLIG-gruppe og gir KommuneTilknytning") {
            val bruker = tilBruker(tilPerson(brukerId, pipRespons(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG, geoKommune())), false)
            assertSoftly(bruker) {
                påkrevdeGrupper shouldContainExactly setOf(STRENGT_FORTROLIG)
                geografiskTilknytning.shouldBeInstanceOf<KommuneTilknytning>()
            }
        }

        it("skjermet bruker krever SKJERMING-gruppe") {
            val bruker = tilBruker(tilPerson(brukerId, pipRespons()), true)
            bruker.påkrevdeGrupper shouldContainExactly setOf(SKJERMING)
        }

        it("skjermet bruker med STRENGT_FORTROLIG krever SKJERMING og STRENGT_FORTROLIG-gruppe") {
            val bruker = tilBruker(tilPerson(brukerId, pipRespons(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)), true)
            bruker.påkrevdeGrupper shouldContainExactlyInAnyOrder setOf(SKJERMING, STRENGT_FORTROLIG)
        }

        it("skjermet bruker med FORTROLIG krever SKJERMING og FORTROLIG-gruppe") {
            val bruker = tilBruker(tilPerson(brukerId, pipRespons(PdlAdressebeskyttelseGradering.FORTROLIG)), true)
            bruker.påkrevdeGrupper shouldContainExactlyInAnyOrder setOf(SKJERMING, FORTROLIG)
        }
    }
})
