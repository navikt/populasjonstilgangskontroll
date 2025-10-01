package no.nav.tilgangsmaskin.regler

import java.time.LocalDate
import java.util.*
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.*
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.FAR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning

data class BrukerBuilder(
    val id: BrukerId,
    var gt: GeografiskTilknytning = UdefinertTilknytning(),
    var historiskeIds: Set<BrukerId> = emptySet(),
    var aktørId: AktørId = AktørId("0000000000000"),
    var grupper: Set<GlobalGruppe> = emptySet(),
    var søsken: Set<FamilieMedlem> = emptySet(),
    var mor: FamilieMedlem? = null,
    var far: FamilieMedlem? = null,
    var barn: Set<FamilieMedlem> = emptySet(),
    var partnere: Set<FamilieMedlem> = emptySet(),
    var oppslagId: String = id.verdi,
    var dødsdato: LocalDate? = null) {
    fun oppslagId(oppslagId: String) = apply { this.oppslagId = oppslagId }
    fun aktørId(aktørId: AktørId) = apply { this.aktørId = aktørId }
    fun gt(gt: GeografiskTilknytning) = apply { this.gt = gt }
    fun kreverMedlemskapI(vararg grupper: GlobalGruppe) = apply { this.grupper = setOf(*grupper) }
    fun barn(barn: Set<BrukerId>) = apply { this.barn = buildSet { barn.forEach { add(FamilieMedlem(it, BARN)) } } }
    fun far(far: BrukerId?) = apply { this.far = far?.let { FamilieMedlem(it, FAR) } }
    fun søsken(søsken: Set<BrukerId>) = apply { this.søsken = buildSet { søsken.forEach { add(FamilieMedlem(it, SØSKEN)) } } }
    fun partnere(partnere: Set<BrukerId>) = apply { this.partnere = buildSet { partnere.forEach { add(FamilieMedlem(it, PARTNER)) } } }
    fun historiske(historiskeIds: Set<BrukerId>) = apply { this.historiskeIds = historiskeIds }
    fun build() = Bruker(
            BrukerIds(id, oppslagId,historiskeIds, aktørId),
            gt, grupper,
            Familie(setOfNotNull(mor, far), barn, søsken, partnere),
            dødsdato)

}


data class AnsattBuilder(
        val id: AnsattId,
        var grupper: Set<EntraGruppe> = emptySet(),
        var globaleGrupper: Set<GlobalGruppe> = emptySet(),
        var oid: UUID = UUID.randomUUID(),
        var bruker: Bruker? = null) {

    fun medMedlemskapI(vararg grupper: GlobalGruppe) = apply { this.grupper += setOf(*grupper).map { it.entraGruppe } }
    fun medMedlemskapI(vararg grupper: EntraGruppe) = apply { this.grupper += setOf(*grupper) }
    fun bruker(bruker: Bruker?) = apply { bruker?.let { this.bruker = it } }
    fun build() = Ansatt(id, bruker, grupper)
}