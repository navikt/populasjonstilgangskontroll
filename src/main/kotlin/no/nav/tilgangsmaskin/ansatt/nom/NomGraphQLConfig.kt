package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create

@Component
class NomGraphQLConfig(@Value("\${nomgraph}") nomHost: String) : RestConfig(create("http://$nomHost$DEFAULT_GRAPHQL_PATH"), "", NOMGRAPH) {

    companion object {
        const val NOMGRAPH = "nomgraph"
        private const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}