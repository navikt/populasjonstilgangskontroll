package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapterTest.GraphQLTestConfig
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@RestClientTest(components = [PdlSyncGraphQLClientAdapter::class, PdlGraphQLConfig::class])
@TestPropertySource(properties = ["PDLGRAPH=pdlgraph"])
@Import(GraphQLTestConfig::class)
@ApplyExtension(SpringExtension::class)
class PdlSyncGraphQLClientAdapterTest : BehaviorSpec() {

    @TestConfiguration
    class GraphQLTestConfig {
        @Bean @Qualifier(PDLGRAPH)
        fun pdlGraphRestClient(b: Builder) =
            b.requestInterceptors {
                it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
            }.build()

        @Bean @Qualifier(PDLGRAPH)
        fun syncPdlGraphQLClient(
            @Qualifier(PDLGRAPH) client: RestClient,
            cfg: PdlGraphQLConfig,
            interceptors: List<SyncGraphQlClientInterceptor>
        ) = HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptors { it.addAll(interceptors) }
            .build()
    }

    @Autowired
    lateinit var adapter: PdlSyncGraphQLClientAdapter
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired
    lateinit var cfg: PdlGraphQLConfig

    init {
        beforeEach { server.reset() }
        afterEach { server.verify() }

        Given("oppslag av partnere fra PDL") {
            When("sivilstand er GIFT") {
                Then("returneres PARTNER") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(sivilstandRespons(Sivilstandstype.GIFT), APPLICATION_JSON))
                    adapter.partnere("Z999999").single().relasjon shouldBe PARTNER
                }
            }
            When("sivilstand er REGISTRERT_PARTNER") {
                Then("returneres PARTNER") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(sivilstandRespons(Sivilstandstype.REGISTRERT_PARTNER), APPLICATION_JSON))
                    adapter.partnere("Z999999").single().relasjon shouldBe PARTNER
                }
            }
            When("sivilstand er SKILT") {
                Then("returneres TIDLIGERE_PARTNER") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(sivilstandRespons(Sivilstandstype.SKILT), APPLICATION_JSON))
                    adapter.partnere("Z999999").single().relasjon shouldBe TIDLIGERE_PARTNER
                }
            }
            When("sivilstand er UGIFT med partner-ident") {
                Then("returneres INGEN") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(sivilstandRespons(Sivilstandstype.UGIFT), APPLICATION_JSON))
                    adapter.partnere("Z999999").single().relasjon shouldBe INGEN
                }
            }
            When("ingen sivilstand har relatertVedSivilstand") {
                Then("returneres tom mengde") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(ingenPartnerRespons(), APPLICATION_JSON))
                    adapter.partnere("Z999999").shouldBeEmpty()
                }
            }
            When("blandet respons med og uten relatertVedSivilstand") {
                Then("filtreres sivilstand uten relatertVedSivilstand ut") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(blandaRespons(), APPLICATION_JSON))
                    adapter.partnere("Z999999") shouldHaveSize 1
                }
            }
        }



        Given("behandlingsnummer-header") {
            When("request sendes til PDL") {
                Then("inneholder header behandlingsnummer med verdi B897") {
                    server.expect(requestTo(cfg.baseUri))
                        .andExpect(header("behandlingsnummer", "B897"))
                        .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.GIFT), APPLICATION_JSON))
                    adapter.partnere("Z999999")
                }
            }
        }

        Given("NOT_FOUND fra PDL") {
            When("PDL returnerer NOT_FOUND-feil") {
                Then("returneres tom mengde") {
                    server.expect(requestTo(cfg.baseUri))
                        .andRespond(withSuccess(notFoundErrorRespons(), APPLICATION_JSON))
                    adapter.partnere("Z999999").shouldBeEmpty()
                }
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

        private fun notFoundErrorRespons() = """
            {
                "errors": [
                    {
                        "message": "Fant ikke person",
                        "locations": [],
                        "path": ["hentPerson"],
                        "extensions": {
                            "code": "not_found",
                            "classification": "ExecutionAborted"
                        }
                    }
                ],
                "data": {
                    "hentPerson": null
                }
            }
        """.trimIndent()
    }
}

