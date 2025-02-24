package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException

class RegelException(val brukerId: BrukerId,val  ansattId: AnsattId, val regel: Regel, val detaljer: String = regel.metadata.detail): ErrorResponseException(FORBIDDEN,  forStatus(FORBIDDEN).apply {
    title = "${regel.metadata.begrunnelse}"
    type = regel.metadata.uri
    detail = detaljer
    properties = mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "kanOverstyres" to regel.erOverstyrbar)
}, null,regel.metadata.begrunnelse.toString(),arrayOf(brukerId.verdi, ansattId.verdi))
