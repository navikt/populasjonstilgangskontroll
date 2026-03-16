package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype
import no.nav.tilgangsmaskin.bruker.pdl.PdlClientBeanConfig.DefaultGraphQlErrorHandler
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.URI

@RestClientTest(components = [PdlSyncGraphQLClientAdapter::class, DefaultGraphQlErrorHandler::class])
@EnableConfigurationProperties(PdlGraphQLConfig::class)
@TestPropertySource(properties = ["pdlgraph.base-uri=http://pdlgraph"])
@Import(PdlSyncGraphQLClientAdapterTest.GraphQLTestConfig::class)
@ApplyExtension(SpringExtension::class)
class PdlSyncGraphQLClientAdapterTest : DescribeSpec() {

    @TestConfiguration
    class GraphQLTestConfig {
        @Bean @Qualifier(PDLGRAPH)
        fun pdlGraphRestClient(b: RestClient.Builder) = b.build()

        @Bean @Qualifier(PDLGRAPH)
        fun syncPdlGraphQLClient(
            @Qualifier(PDLGRAPH) client: RestClient,
            cfg: PdlGraphQLConfig,
            interceptors: List<SyncGraphQlClientInterceptor>
        ) = HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptors { it.addAll(interceptors) }
            .build()

        @Bean
        fun errorHandler() = ErrorHandler { _, response -> throw IrrecoverableRestException(
            response.statusCode as HttpStatus, URI.create("http://pdlgraph"), response.statusText
        )}
    }

    @Autowired
    lateinit var adapter: PdlSyncGraphQLClientAdapter
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired
    lateinit var cfg: PdlGraphQLConfig

    init {
        beforeEach { server.reset() }

        describe("partnere") {

            it("returnerer PARTNER for GIFT sivilstand") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.GIFT), APPLICATION_JSON))

                val result = adapter.partnere("Z999999")
                result.single().relasjon shouldBe PARTNER
                server.verify()
            }

            it("returnerer PARTNER for REGISTRERT_PARTNER") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.REGISTRERT_PARTNER), APPLICATION_JSON))

                val result = adapter.partnere("Z999999")
                result.single().relasjon shouldBe PARTNER
                server.verify()
            }

            it("returnerer TIDLIGERE_PARTNER for SKILT sivilstand") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.SKILT), APPLICATION_JSON))

                val result = adapter.partnere("Z999999")

                result.single().relasjon shouldBe TIDLIGERE_PARTNER
                server.verify()
            }

            it("returnerer INGEN for UGIFT sivilstand med partner-ident") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.UGIFT), APPLICATION_JSON))

                val result = adapter.partnere("Z999999")

                result.single().relasjon shouldBe INGEN
                server.verify()
            }

            it("returnerer tom mengde når ingen sivilstand har relatertVedSivilstand") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(ingenPartnerRespons(), APPLICATION_JSON))

                adapter.partnere("Z999999").shouldBeEmpty()
                server.verify()
            }

            /*
            it("returnerer tom mengde ved 404") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withStatus(NOT_FOUND))

                adapter.partnere("Z999999").shouldBeEmpty()
                server.verify()
            }
            
             */

            it("filtrerer ut sivilstand uten relatertVedSivilstand") {
                server.expect(requestTo(cfg.baseUri))
                    .andRespond(withSuccess(blandaRespons(), APPLICATION_JSON))

                val result = adapter.partnere("Z999999")

                result shouldHaveSize 1
                server.verify()
            }
        }
    }

    companion object {
        private fun sivilstandRespons(type: Sivilstandstype) = """
            {
                "data": {
                    "hentPerson": {
                        "sivilstand": [
                            {
                                "type": "$type",
                                "relatertVedSivilstand": "12345678901",
                                "gyldigFraOgMed": "2020-01-01",
                                "bekreftelsesdato": null
                            }
                        ]
                    }
                }
            }
        """.trimIndent()

        private fun ingenPartnerRespons() = """
            {
                "data": {
                    "hentPerson": {
                        "sivilstand": [
                            {
                                "type": "UGIFT",
                                "relatertVedSivilstand": null,
                                "gyldigFraOgMed": null,
                                "bekreftelsesdato": null
                            }
                        ]
                    }
                }
            }
        """.trimIndent()

        private fun blandaRespons() = """
            {
                "data": {
                    "hentPerson": {
                        "sivilstand": [
                            {
                                "type": "UGIFT",
                                "relatertVedSivilstand": null,
                                "gyldigFraOgMed": null,
                                "bekreftelsesdato": null
                            },
                            {
                                "type": "GIFT",
                                "relatertVedSivilstand": "12345678901",
                                "gyldigFraOgMed": "2020-01-01",
                                "bekreftelsesdato": null
                            }
                        ]
                    }
                }
            }
        """.trimIndent()
    }
}

