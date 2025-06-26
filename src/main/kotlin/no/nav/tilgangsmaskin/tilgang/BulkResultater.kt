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
}