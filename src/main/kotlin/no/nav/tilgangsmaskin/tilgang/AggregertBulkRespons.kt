package no.nav.tilgangsmaskin.tilgang

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

data class AggregertBulkRespons(val ansattId: AnsattId, val resultater: Set<EnkeltBulkRespons> = emptySet()) {
    data class EnkeltBulkRespons(val oppslagId: String, @JsonIgnore val httpStatus: HttpStatus, val detaljer: Any? = null ) {
        constructor(e: RegelException) : this(e.bruker.oppslagId, e.status,e.body)
        val status = httpStatus.value()

        companion object {
            fun ok(brukerId: String) = EnkeltBulkRespons(brukerId, NO_CONTENT)
           // fun ok(brukerId: BrukerId) = ok(brukerId.verdi)
        }

        override fun toString(): String =
            "EnkeltBulkRespons(oppslagId='${oppslagId.maskFnr()}', httpStatus=$httpStatus, detaljer=$detaljer, status=$status)"
    }
    @JsonIgnore
    val ukjente = filter(NOT_FOUND)
    @JsonIgnore
    val godkjente = filter(NO_CONTENT)
    @JsonIgnore
    val avviste = filter(FORBIDDEN)

    private fun filter(status: HttpStatus) = resultater.filter { it.httpStatus == status }.toSet()

    override fun toString(): String =
        "AggregertBulkRespons(ansattId=$ansattId, resultater=$resultater)"
}