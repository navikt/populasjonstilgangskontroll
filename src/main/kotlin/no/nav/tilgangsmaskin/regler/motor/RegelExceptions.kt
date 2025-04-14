package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelBeskrivelse.Companion.DETAIL_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelBeskrivelse.Companion.TYPE_URI
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException
import java.net.URI

class RegelException(
    val brukerId: BrukerId,
    val ansattId: AnsattId,
    val regel: Regel,
    messageCode: String = DETAIL_MESSAGE_CODE,
    arguments: Array<String> = arrayOf(ansattId.verdi, brukerId.verdi, regel.avvisningTekst)
) :
    ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
        title = "${regel.kode}"
        type = TYPE_URI
        instance = URI.create("${ansattId.verdi}/${brukerId.verdi}")
        properties = mapOf(
            "brukerIdent" to brukerId.verdi,
            "navIdent" to ansattId.verdi,
            "kanOverstyres" to regel.erOverstyrbar
        )
    }, null, messageCode, arguments) {
    constructor(messageCode: String, arguments: Array<String>, e: RegelException) : this(
        e.brukerId,
        e.ansattId,
        e.regel,
        messageCode,
        arguments
    )

    val kode = regel.kode
}

class BulkRegelException(val ansattId: AnsattId, val exceptions: List<RegelException>) :
    RuntimeException("Følgende ${exceptions.size} fødselsnummer ble avvist ved bulk-kjøring av regler for $ansattId ${exceptions.map { it.brukerId to it.regel.kode }}")
