package no.nav.tilgangsmaskin.tilgang

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.mask
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

data class AggregertBulkRespons(val ansattId: AnsattId, val resultater: Set<EnkeltBulkRespons> = emptySet()) {
    data class EnkeltBulkRespons(val brukerId: String, @JsonIgnore val httpStatus: HttpStatus, val detaljer: Any? = null ) {
        constructor(e: RegelException) : this(e.bruker.oppslagId, e.status,e.body)
        val status = httpStatus.value()

        companion object {
            fun ok(brukerId: String) = EnkeltBulkRespons(brukerId, NO_CONTENT)
           // fun ok(brukerId: BrukerId) = ok(brukerId.verdi)
        }

        override fun toString(): String =
            "${javaClass.simpleName}(oppslagId='${brukerId.mask()}', httpStatus=$httpStatus, detaljer=$detaljer, status=$status)"
    }
    @JsonIgnore
    val ukjente = filter(NOT_FOUND)
    @JsonIgnore
    val godkjente = filter(NO_CONTENT)
    @JsonIgnore
    val avviste = filter(FORBIDDEN)

    private fun filter(status: HttpStatus) = resultater.filter { it.httpStatus == status }.toSet()

    override fun toString(): String =
        "${javaClass.simpleName}(ansattId=$ansattId, resultater=$resultater)"
}