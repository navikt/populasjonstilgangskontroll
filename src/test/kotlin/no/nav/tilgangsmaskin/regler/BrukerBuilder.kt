package no.nav.tilgangsmaskin.regler

import java.time.LocalDate
import java.util.*
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIds
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.*
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.BARN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.FAR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertTilknytning
import no.nav.tilgangsmaskin.regler.ansatte.ansattId

data class BrukerBuilder(
        val id: BrukerId,
        val gt: GeografiskTilknytning = udefinertTilknytning,
        var aktørId: AktørId? = null,
        var grupper: Set<GlobalGruppe> = emptySet(),
        var søsken: Set<FamilieMedlem> = emptySet(),
        var mor: FamilieMedlem? = null,
        var far: FamilieMedlem? = null,
        var barn: Set<FamilieMedlem> = emptySet(),
        var partnere: Set<FamilieMedlem> = emptySet(),
        var dødsdato: LocalDate? = null) {
    fun grupper(vararg grupper: GlobalGruppe) = apply { this.grupper = setOf(*grupper) }
    fun barn(barn: Set<BrukerId>) = apply { this.barn = barn.map { FamilieMedlem(it, BARN) }.toSet() }
    fun mor(mor: BrukerId?) = apply { mor?.let { this.mor = FamilieMedlem(it, MOR) } }
    fun far(far: BrukerId?) = apply { far?.let { this.far = FamilieMedlem(far, FAR) } }
    fun søsken(søsken: Set<BrukerId>) = apply { this.søsken = søsken.map { FamilieMedlem(it, SØSKEN) }.toSet() }
    fun partnere(partnere: Set<BrukerId>) =
        apply { this.partnere = partnere.map { FamilieMedlem(it, PARTNER) }.toSet() }

    fun dødsdato(dato: LocalDate) = apply { this.dødsdato = dato }
    fun aktørId(id: AktørId) = apply { this.aktørId = id }
    fun build() = Bruker(
            BrukerIds(id, aktørId = aktørId),
            gt,
            grupper,
            Familie(setOfNotNull(mor, far), barn, søsken, partnere),
            dødsdato)
}

data class AnsattBuilder(
        var grupper: Set<EntraGruppe> = emptySet(),
        var id: AnsattId = ansattId,
        var oid: UUID = UUID.randomUUID(),
        var bruker: Bruker? = null) {

    fun id(id: AnsattId) = apply { this.id = id }
    fun grupper(vararg grupper: EntraGruppe) = apply { this.grupper = setOf(*grupper) }
    fun bruker(bruker: Bruker?) = apply { bruker?.let { this.bruker = it } }
    fun build() = Ansatt(AnsattIds(id, oid), bruker, grupper)
}