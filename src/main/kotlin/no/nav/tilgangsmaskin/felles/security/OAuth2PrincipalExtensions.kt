package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.NAVIDENT
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal

fun OAuth2AuthenticatedPrincipal.requiredAnsattId() =
    requireNotNull(getAttribute<String>(NAVIDENT)) { "Mangler ansattId i OBO-token" }.let(::AnsattId)
