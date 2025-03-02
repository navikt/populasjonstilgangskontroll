package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.Companion.DETAIL_MESSAGE_CODE
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException
import java.net.URI

class RegelException(val brukerId: BrukerId, val  ansattId: AnsattId, val regel: Regel, messageCode: String = DETAIL_MESSAGE_CODE, arguments: Array<String> = arrayOf(ansattId.verdi, brukerId.verdi,regel.metadata.begrunnelse.årsak)) :
    ErrorResponseException(FORBIDDEN,  forStatus(FORBIDDEN).apply {
        title = "${regel.metadata.begrunnelse}"
        type = regel.metadata.uri
        instance = URI.create("${ansattId.verdi}/${brukerId.verdi}")
        properties = mapOf(
            "brukerIdent" to brukerId.verdi,
            "navIdent" to ansattId.verdi,
            "kanOverstyres" to regel.erOverstyrbar)
    }, null,messageCode,arguments) {
    constructor(e: RegelException, messageCode: String, arguments: Array<String>) : this(e.brukerId, e.ansattId, e.regel, messageCode, arguments)
}
class BulkRegelException(val exceptions: List<RegelException>) : ErrorResponseException(FORBIDDEN, forStatus(FORBIDDEN).apply {
    title = "Feil ved kjøring av bulk regler"
    type = URI.create("https://tilgangsmaskin.nav.no/bulk")
    instance = URI.create("bulk")
    properties = mapOf(
        "antallFeil" to exceptions.size,
        "feil" to exceptions.map { it.message }
    )
}, null, DETAIL_MESSAGE_CODE, arrayOf(exceptions.size.toString())) {
}
