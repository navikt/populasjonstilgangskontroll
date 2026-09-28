package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.nom.NomGraphQLConfig.Companion.NOMGRAPH
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.graphql.client.GraphQlClient
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@RestClientTest
@ContextConfiguration(classes = [NomSyncGraphQLClientAdapter::class, NomGraphQLConfig::class])
@Import(NomSyncGraphQLClientAdapterTest.GraphQLTestConfig::class)
@TestPropertySource(properties = ["nomgraph=localhost"])
class NomSyncGraphQLClientAdapterTest(
    private val adapter: NomSyncGraphQLClientAdapter,
    private val server: MockRestServiceServer,
    private val cfg: NomGraphQLConfig,
) : BehaviorSpec() {

    @TestConfiguration
    class GraphQLTestConfig {
        @Bean
        @Qualifier(NOMGRAPH)
        fun nomGraphRestClient(builder: Builder) = builder.build()

        @Bean
        @Qualifier(NOMGRAPH)
        fun nomGraphSyncGraphQLClient(
            @Qualifier(NOMGRAPH) client: RestClient,
            cfg: NomGraphQLConfig,
        ): GraphQlClient =
            HttpSyncGraphQlClient.builder(client)
                .url(cfg.baseUri)
                .build()
    }

    init {
        beforeEach { server.reset() }
        afterEach { server.verify() }

        Given("oppslag av ressurs i NOM") {
            When("NOM ikke finner ressursen") {
                Then("returneres null") {
                    server.expect(requestTo(cfg.baseUri))
                        .andRespond(withSuccess("""{"data":{"ressurs":null}}""", APPLICATION_JSON))

                    adapter.lederForAnsatt("Z999999") shouldBe null
                }
            }
        }
    }
}
