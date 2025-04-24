package no.nav.tilgangsmaskin.regler.motor

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.Metadata.Companion.DETAIL_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.Metadata.Companion.TYPE_URI
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException

class RegelException(
        val brukerId: BrukerId,
        val ansattId: AnsattId,
        val regel: Regel,
        messageCode: String = DETAIL_MESSAGE_CODE,
        arguments: Array<String> = arrayOf(ansattId.verdi, brukerId.verdi, regel.begrunnelse)) :
    ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
        title = regel.kode
        type = TYPE_URI
        instance = URI.create("${ansattId.verdi}/${brukerId.verdi}")
        properties = mapOf(
                "brukerIdent" to brukerId.verdi,
                "navIdent" to ansattId.verdi,
                "begrunnelse" to regel.begrunnelse,
                "kanOverstyres" to regel.erOverstyrbar)
    }, null, messageCode, arguments) {
    constructor(messageCode: String, arguments: Array<String>, e: RegelException) : this(
            e.brukerId,
            e.ansattId,
            e.regel,
            messageCode,
            arguments)
}

class BulkRegelException(private val ansattId: AnsattId, val exceptions: List<RegelException>) :
    ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
        title = exceptions.map { it.regel.kode }.toSet().joinToString()
        type = TYPE_URI
        properties = mapOf(
                "navIdent" to ansattId.verdi,
                "avvisninger" to exceptions.size,
                "begrunnelser" to exceptions.map {
                    mapOf(
                            "type" to TYPE_URI,
                            "title" to it.regel.kode,
                            "instance"  to URI.create("${ansattId.verdi}/${it.brukerId.verdi}")
                            "brukerIdent" to it.brukerId.verdi,
                            "navIdent" to ansattId.verdi,
                            "begrunnelse" to it.regel.begrunnelse,
                            "kanOverstyres" to it.regel.erOverstyrbar
                         )
                }.toList())
    }, null)

