package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomConfig.Companion.NOM
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.net.URI

@Configuration
class NomClientBeanConfig {

    @Bean
    @Qualifier(NOM)
    fun electorRestClient(b: RestClient.Builder, @Value("\${elector.get.url}") uri: URI) =
        b.baseUrl(uri)
            .requestInterceptors {
            }.build()
}