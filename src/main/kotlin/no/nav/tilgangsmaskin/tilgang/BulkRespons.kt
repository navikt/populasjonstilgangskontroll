package no.nav.tilgangsmaskin.tilgang

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

data class BulkRespons(val ansattId: AnsattId, val resultater: Set<BulkResultat> = emptySet()) {
    data class BulkResultat(val brukerId: BrukerId, @JsonIgnore val httpStatus: HttpStatus, val detaljer: Any? = null ) {
        constructor(e: RegelException) : this(e.bruker.brukerId, e.status,e.body)
        val status = httpStatus.value()

        companion object {
            fun ok(brukerId: BrukerId) = BulkResultat(brukerId, NO_CONTENT)
        }
    }
    @JsonIgnore
    val ukjente = filter(NOT_FOUND)
    @JsonIgnore
    val godkjente = filter(NO_CONTENT)
    @JsonIgnore
    val avviste = filter(FORBIDDEN)

    private fun filter(status: HttpStatus) = resultater.filter { it.httpStatus == status }.toSet()

}