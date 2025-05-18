package no.nav.tilgangsmaskin.regler.motor

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.DETAIL_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.TYPE_URI
import org.slf4j.MDC
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException

class RegelException(val ansatt: Ansatt,
                     val bruker: Bruker,
                     val regel: Regel,
                     messageCode: String = DETAIL_MESSAGE_CODE,
                     arguments: Array<String> = arrayOf(
                             ansatt.ansattId.verdi,
                             bruker.brukerId.verdi,
                             regel.begrunnelse)) :
    ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
        title = regel.kode
        type = TYPE_URI
        instance = URI.create("${ansatt.ansattId.verdi}/${bruker.brukerId.verdi}")
        properties = entries(bruker.brukerId, ansatt.ansattId, regel)
    }, null, messageCode, arguments) {
    constructor(messageCode: String, arguments: Array<String>, e: RegelException) : this(
            e.ansatt, e.bruker, e.regel, messageCode, arguments)
}

private fun entries(brukerId: BrukerId, ansattId: AnsattId, regel: Regel) = mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "begrunnelse" to regel.begrunnelse,
        "kanOverstyres" to regel.erOverstyrbar)

class BulkRegelException(private val ansattId: AnsattId, val exceptions: List<RegelException>) :
    ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
        title = exceptions.map { it.regel.kode }.toSet().joinToString()
        type = TYPE_URI
        properties = mapOf(
                "begrunnelser" to exceptions.map {
                    entries(it.bruker.brukerId, ansattId, it.regel) +
                            mapOf(
                                    "type" to TYPE_URI,
                                    "title" to it.regel.kode,
                                    "instance" to URI.create("${ansattId.verdi}/${it.bruker.brukerId.verdi}"),
                                 )
                }.toList())
    }, null)

