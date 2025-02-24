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
   // detail = detaljer
    properties = mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "kanOverstyres" to regel.erOverstyrbar)
}, null,DETAIL_MESSAGE_CODE,arrayOf(brukerId.verdi, ansattId.verdi, regel.metadata.begrunnelse.årsak))


