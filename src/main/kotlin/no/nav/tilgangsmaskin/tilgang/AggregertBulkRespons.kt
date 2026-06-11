package no.nav.tilgangsmaskin.tilgang

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.http.HttpStatus

data class AggregertBulkRespons(val ansattId: AnsattId, val resultater: Set<EnkeltBulkRespons> = emptySet()) {
    data class EnkeltBulkRespons(val brukerId: String, @JsonIgnore val httpStatus: HttpStatus, val detaljer: Any? = null ) {
        constructor(e: RegelException) : this(e.bruker.oppslagId, e.status,e.body)
        val status = httpStatus.value()

        companion object {
            fun ok(brukerId: String) =
                EnkeltBulkRespons(brukerId, HttpStatus.NO_CONTENT)
        }

        @NoCoverageAnalysis
        override fun toString(): String =
            "${javaClass.simpleName}(oppslagId='${brukerId.maskFnr()}', httpStatus=$httpStatus, detaljer=$detaljer, status=$status)"
    }
    @JsonIgnore
    val ukjente = filter(HttpStatus.NOT_FOUND)
    @JsonIgnore
    val godkjente = filter(HttpStatus.NO_CONTENT)
    @JsonIgnore
    val avviste = filter(HttpStatus.FORBIDDEN)

    private fun filter(status: HttpStatus) = resultater.filter { it.httpStatus == status }.toSet()

    @NoCoverageAnalysis
    override fun toString(): String =
        "${javaClass.simpleName}(ansattId=$ansattId, resultater=$resultater)"
}