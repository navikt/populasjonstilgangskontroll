package no.nav.tilgangsmaskin.felles.security

import no.nav.sikkerhetstjenesten.felles.security.AuthContext.Companion.NAVIDENT
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal

fun OAuth2AuthenticatedPrincipal.ansattId() =
    requireNotNull(getAttribute<String>(NAVIDENT)) { "Mangler ansattId i OBO-token" }.let(::AnsattId)
