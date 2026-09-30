package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_ANSATT_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_ENHETER_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_PING_PATH
import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkClient.Companion.KODEVERK_PING_PATH
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI

@Component
class KodeverkConfig(@Value("\${spring.http.serviceclient.kodeverk.base-url}") baseUrl: URI) : RestConfig(baseUrl, KODEVERK_PING_PATH, KODEVERK) {

    companion object {
        const val KODEVERK  = "kodeverk"
    }
}