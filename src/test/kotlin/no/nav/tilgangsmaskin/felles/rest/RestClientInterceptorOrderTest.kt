package no.nav.tilgangsmaskin.felles.rest

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.nom.NomBeanConfig
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphBeanConfig
import no.nav.tilgangsmaskin.felles.security.OAuth2SecurityBeanConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.service.registry.HttpServiceGroupConfigurer.ClientCallback
import org.springframework.web.service.registry.HttpServiceGroupConfigurer.Groups
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.Logbook
import org.zalando.logbook.Precorrelation
import org.zalando.logbook.Sink
import org.zalando.logbook.attributes.AttributeExtractor
import org.zalando.logbook.attributes.HttpAttributes
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

class RestClientInterceptorOrderTest {
    private val extractor = mockk<AttributeExtractor> {
        every { extract(any<HttpRequest>()) } returns HttpAttributes.EMPTY
        every { extract(any(), any()) } returns HttpAttributes.EMPTY
    }
    private val sink = mockk<Sink>(relaxed = true) {
        every { isActive } returns true
    }
    private val logbookInterceptor = LogbookClientHttpRequestInterceptor(
        Logbook.builder().attributeExtractor(extractor).sink(sink).build()
    )
    private val logbookProvider = DefaultListableBeanFactory().apply {
        registerSingleton("logbookInterceptor", logbookInterceptor)
    }.getBeanProvider(LogbookClientHttpRequestInterceptor::class.java)
    private val manager = mockk<OAuth2AuthorizedClientManager> {
        every { authorize(any()) } returns mockk<OAuth2AuthorizedClient> {
            every { accessToken } returns OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-token", null, null
            )
        }
    }

    @Test
    fun `grouped clients add token before Logbook`() {
        verifyLogging { builder ->
            val groups = mockk<Groups<RestClient.Builder>>(relaxed = true) {
                every { forEachClient(any<ClientCallback<RestClient.Builder>>()) } answers {
                    firstArg<ClientCallback<RestClient.Builder>>().withClient(mockk(), builder)
                }
            }
            OAuth2SecurityBeanConfig(logbookProvider).oauth2GroupConfigurer(manager).configureGroups(groups)
            builder.build()
        }
    }

    @Test
    fun `PDL GraphQL adds token before Logbook`() {
        verifyLogging { builder ->
            PdlGraphBeanConfig().pdlGraphRestClient(builder, manager, mockk())
        }
    }

    @Test
    fun `NOM GraphQL adds token before Logbook`() {
        verifyLogging { builder ->
            NomBeanConfig().nomGraphRestClient(builder, manager, mockk())
        }
    }

    private fun verifyLogging(configure: (RestClient.Builder) -> RestClient) {
        val builder = RestClient.builder()
        RestBeanConfig(mockk(), mockk(), logbookProvider)
            .restClientCustomizer(mockk()).customize(builder)
        val server = MockRestServiceServer.bindTo(builder).build()
        val client = configure(builder)
        builder.requestInterceptors { interceptors ->
            interceptors.count { it === logbookInterceptor } shouldBe 1
            interceptors.filterIsInstance<OAuth2ClientHttpRequestInterceptor>().forEach {
                it.setClientRegistrationIdResolver { "test" }
                it.setPrincipalResolver {
                    UsernamePasswordAuthenticationToken.authenticated("test", null, emptyList())
                }
            }
        }
        server.expect(requestTo("https://example.test"))
            .andExpect(header(AUTHORIZATION, "Bearer test-token"))
            .andRespond(withSuccess())

        client.get().uri("https://example.test").retrieve().toBodilessEntity()

        verify(exactly = 1) {
            extractor.extract(match<HttpRequest> { it.headers.getFirst(AUTHORIZATION) == "Bearer test-token" })
            sink.write(any<Precorrelation>(), match<HttpRequest> { it.headers.getFirst(AUTHORIZATION) == "XXX" })
        }
        server.verify()
    }
}
