package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.ENTRA_CACHES
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM_CACHE
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.Generated

@Generated
enum class Caches(vararg val  caches: CachableConfig) {
    PDL(*PDL_CACHES.toTypedArray()),
    SKJERMING(SKJERMING_CACHE),
    OID(OID_CACHE),
    VERGE(VERGE_CACHE),
    OPPFØLGING(OPPFØLGING_CACHE),
    NOM(NOM_CACHE),
    GRAPH(*ENTRA_CACHES.toTypedArray());
}