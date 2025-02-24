package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.Companion.DETAIL_MESSAGE_CODE
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.web.ErrorResponseException

class RegelException(val brukerId: BrukerId,val  ansattId: AnsattId, val regel: Regel): ErrorResponseException(FORBIDDEN,  forStatus(FORBIDDEN).apply {
    title = "${regel.metadata.begrunnelse}"
    type = regel.metadata.uri
    properties = mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "kanOverstyres" to regel.erOverstyrbar)
}, null,null,arrayOf(ansattId.verdi, brukerId.verdi,regel.metadata.begrunnelse.årsak))


