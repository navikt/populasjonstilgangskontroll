package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson

internal object PdlPersonMapperTestFixture {
    const val BRUKER_ID = "08526835670"
    const val AKTOR_ID = "1234567890123"
    val BARN = BrukerId("01010112345")
    val MOR = BrukerId("01010198765")
    val FAR = BrukerId("01010154321")

    fun identer(
        fnr: String = BRUKER_ID,
        aktor: String = AKTOR_ID,
        historiske: List<Pair<String, PdlIdentGruppe>> = emptyList(),
    ) = PdlIdenter(
        buildList {
            add(PdlIdent(fnr, false, FOLKEREGISTERIDENT))
            add(PdlIdent(aktor, false, AKTORID))
            historiske.forEach { (ident, gruppe) -> add(PdlIdent(ident, true, gruppe)) }
        }
    )

    fun pdlRespons(
        person: PdlPerson = PdlPerson(),
        geo: PdlGeografiskTilknytning? = PdlGeografiskTilknytning(UDEFINERT),
        identer: PdlIdenter = identer(),
    ) = PdlPipRespons(person, identer, geo)
}
