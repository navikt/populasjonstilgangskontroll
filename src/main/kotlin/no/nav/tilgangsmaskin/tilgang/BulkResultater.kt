package no.nav.tilgangsmaskin.tilgang

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.http.HttpStatus

data class BulkResultater(val ansattId: AnsattId, val resultater: Set<BulkResultat>) {
    data class BulkResultat(val brukerId: BrukerId, @JsonIgnore val httpStatus: HttpStatus, val detaljer: Any? = null ) {
        constructor(e: RegelException) : this(e.bruker.brukerId, e.status,e.body)
        val status = httpStatus.value()
    }
    @JsonIgnore
    val ukjente = resultater.filter { it.httpStatus == HttpStatus.NOT_FOUND }.map { it.brukerId }.toSet()
    @JsonIgnore
    val godkjente = resultater.filter { it.httpStatus.is2xxSuccessful }.map { it.brukerId }.toSet()
    @JsonIgnore
    val avviste = resultater.filter { it.httpStatus == HttpStatus.FORBIDDEN }.map { it.brukerId }.toSet()
}