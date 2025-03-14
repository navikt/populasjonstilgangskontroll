package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Type.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster.Companion.isProd
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requireDigits

class Bruker(
    val brukerId: BrukerId,
    val geoTilknytning: GeoTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    val familie: Familie = INGEN,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()) {

    @JsonIgnore
    val familieMedlemmer = familie.familieMedlemmer

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    override fun toString() = "${javaClass.simpleName} [ident=$brukerId, geoTilknytning=$geoTilknytning,  gruppeKrav=$gruppeKrav,identifikatorer=$historiskeIdentifikatorer]"
}

@JvmInline
value class AktørId(val verdi: String){
    init {
        requireDigits(verdi, 13)
    }
}

@JvmInline
value class BrukerId(@JsonValue val verdi: String) {

    enum class Type { DNR, FNR, TENOR }

    fun type() = when (verdi[0]) {
        '8', '9' -> Type.TENOR
        '4', '5' -> Type.DNR
        else -> Type.FNR
    }

    init {
        with(verdi) {
            requireDigits(this, 11)
            if (isProd) {
                require(mod11(W1, this) == this[9] - '0') { "Første kontrollsiffer $this[9] ikke validert" }
                require(mod11(W2, this) == this[10] - '0') { "Andre kontrollsiffer $this[10] ikke validert" }
            }
        }
    }

    companion object {

        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(weights: IntArray, brukerId: String) =
            with(weights.indices.sumOf { weights[it] * (brukerId[(weights.size - 1 - it)] - '0') } % 11) {
                when (this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(brukerId)
                    else -> 11 - this
                }
            }
    }

    override fun toString() =
        "${javaClass.simpleName} [BrukerId=${this.mask()}]"

}

data class Familie(val foreldre: List<FamilieMedlem> = emptyList(), val barn: List<FamilieMedlem> = emptyList()) {
   @JsonIgnore
   val familieMedlemmer = foreldre + barn
    companion object {
        val INGEN = Familie()
    }
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon)
}

sealed class GeoTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    @JvmInline
    value class Kommune(val verdi: String)  {
        init {
            requireDigits(verdi, 4)
        }
    }

    @JvmInline
    value class Bydel(val verdi: String)  {
        init {
            requireDigits(verdi, 6)
        }
    }
    data class KommuneTilknytning(val kommune: Kommune) : GeoTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeoTilknytning(BYDEL)
    class UkjentBosted : GeoTilknytning(UKJENT_BOSTED)
    data class UtenlandskTilknytning(val land: CountryCode) : GeoTilknytning(UTLAND)
    class UdefinertTilknytning : GeoTilknytning(UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}