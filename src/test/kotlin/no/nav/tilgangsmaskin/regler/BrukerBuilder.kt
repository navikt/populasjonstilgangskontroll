package no.nav.tilgangsmaskin.regler

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import java.time.LocalDate
import java.util.*

data class BrukerBuilder(
    val id: BrukerId,
    var gt: GeografiskTilknytning = UdefinertTilknytning(),
    var historiskeIds: Set<BrukerId> = emptySet(),
    var aktørId: AktørId = AktørId("0000000000000"),
    var grupper: Set<EntraGlobalGruppe> = emptySet(),
    var søsken: Set<BrukerId> = emptySet(),
    var mor: BrukerId? = null,
    var far: BrukerId? = null,
    var barn: Set<BrukerId> = emptySet(),
    var partnere: Set<BrukerId> = emptySet(),
    var oppslagId: String = id.verdi,
    var dødsdato: LocalDate? = null) {
    fun dødsdato(dødsdato: LocalDate?) = apply { this.dødsdato = dødsdato }
    fun oppslagId(oppslagId: String) = apply { this.oppslagId = oppslagId }
    fun aktørId(aktørId: AktørId) = apply { this.aktørId = aktørId }
    fun gt(gt: GeografiskTilknytning) = apply { this.gt = gt }
    fun kreverMedlemskapI(vararg grupper: EntraGlobalGruppe) = apply { this.grupper = setOf(*grupper) }
    fun barn(barn: Set<BrukerId>) = apply { this.barn = barn }
    fun far(far: BrukerId?) = apply { this.far = far }
    fun søsken(søsken: Set<BrukerId>) = apply { this.søsken = søsken }
    fun partnere(partnere: Set<BrukerId>) = apply { this.partnere = partnere }
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
    var globaleGrupper: Set<EntraGlobalGruppe> = emptySet(),
    var oid: UUID = UUID.randomUUID(),
    var bruker: Bruker? = null) {

    fun medMedlemskapI(vararg grupper: EntraGlobalGruppe) = apply { this.grupper += setOf(*grupper).map { it.entraGruppe } }
    fun medMedlemskapI(vararg grupper: EntraGruppe) = apply { this.grupper += setOf(*grupper) }
    fun bruker(bruker: Bruker?) = apply { bruker?.let { this.bruker = it } }
    fun build() = Ansatt(id, bruker, grupper)
}