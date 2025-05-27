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
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.FAR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertTilknytning

data class BrukerBuilder(
        val id: BrukerId,
        var gt: GeografiskTilknytning = udefinertTilknytning,
        var historiske: Set<BrukerId> = emptySet(),
        var aktørId: AktørId? = null,
        var grupper: Set<GlobalGruppe> = emptySet(),
        var søsken: Set<FamilieMedlem> = emptySet(),
        var mor: FamilieMedlem? = null,
        var far: FamilieMedlem? = null,
        var barn: Set<FamilieMedlem> = emptySet(),
        var partnere: Set<FamilieMedlem> = emptySet(),
        var dødsdato: LocalDate? = null) {
    fun gt(gt: GeografiskTilknytning) = apply { this.gt = gt }
    fun kreverMedlemskapI(vararg grupper: GlobalGruppe) = apply { this.grupper = setOf(*grupper) }
    fun barn(barn: Set<BrukerId>) = apply { this.barn = barn.tilFamilieMedlemmer(BARN) }
    fun mor(mor: BrukerId?) = apply { this.mor = mor?.tilFamilieMedlem(MOR) }
    fun far(far: BrukerId?) = apply { this.far = far?.tilFamilieMedlem(FAR) }
    fun søsken(søsken: Set<BrukerId>) = apply { this.søsken = søsken.tilFamilieMedlemmer(SØSKEN) }
    fun partnere(partnere: Set<BrukerId>) = apply { this.partnere = partnere.tilFamilieMedlemmer(PARTNER) }
    fun historiske(historiske: Set<BrukerId>) = apply { this.historiske = historiske }
    fun dødsdato(dato: LocalDate) = apply { this.dødsdato = dato }
    fun aktørId(id: AktørId) = apply { this.aktørId = id }
    fun build() = Bruker(
            BrukerIds(id, historiske, aktørId),
            gt, grupper,
            Familie(setOfNotNull(mor, far), barn, søsken, partnere),
            dødsdato)


    private fun Set<BrukerId>.tilFamilieMedlemmer(relasjon: FamilieRelasjon) =
        map { FamilieMedlem(it, relasjon) }.toSet()

    private fun BrukerId.tilFamilieMedlem(relasjon: FamilieRelasjon) =
        FamilieMedlem(this, relasjon)
}

data class AnsattBuilder(
        val id: AnsattId,
        var grupper: Set<EntraGruppe> = emptySet(),
        var globaleGrupper: Set<GlobalGruppe> = emptySet(),
        var oid: UUID = UUID.randomUUID(),
        var bruker: Bruker? = null) {

    fun medMedlemskapI(vararg grupper: GlobalGruppe) = apply { this.grupper += grupper.map { it.entraGruppe }.toSet() }
    fun medMedlemskapI(vararg grupper: EntraGruppe) = apply { this.grupper += setOf(*grupper) }
    fun bruker(bruker: Bruker?) = apply { bruker?.let { this.bruker = it } }
    fun build() = Ansatt(id, bruker, grupper)
}