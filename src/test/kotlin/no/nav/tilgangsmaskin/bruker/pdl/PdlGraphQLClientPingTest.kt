package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLClientPingTest.TestConfig
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient.Builder

@RestClientTest(components = [PdlGraphQLConfig::class])
@TestPropertySource(properties = ["PDLGRAPH=pdlgraph"])
@Import(TestConfig::class)
@ApplyExtension(SpringExtension::class)
class PdlGraphQLClientPingTest : BehaviorSpec() {

    @TestConfiguration
    class TestConfig {
        @Bean
        fun pdlGraphQLClient(b: Builder, cfg: PdlGraphQLConfig) =
            createClient<PdlGraphQLPingClient>(cfg, b)
    }

    @Autowired @Qualifier("pdlGraphQLClient") lateinit var client: PdlGraphQLPingClient
    @Autowired lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: PdlGraphQLConfig

    init {
        beforeEach { server.reset() }
        afterEach { server.verify() }

        Given("ping mot PDL GraphQL-endepunkt") {
            When("ping kalles") {
                Then("sendes OPTIONS-request") {
                    server.expect(requestTo(cfg.baseUri))
                        .andExpect(method(OPTIONS))
                        .andRespond(withSuccess())
                    client.ping()
                }
            }
        }
    }
}

