package no.nav.tilgangsmaskin.felles.rest

import com.nimbusds.jwt.SignedJWT.parse
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Component
import org.zalando.logbook.Correlation
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.HttpResponse
import org.zalando.logbook.Logbook
import org.zalando.logbook.Precorrelation
import org.zalando.logbook.attributes.AttributeExtractor
import org.zalando.logbook.attributes.HttpAttributes
import org.zalando.logbook.attributes.HttpAttributes.EMPTY
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink
import org.zalando.logbook.core.StatusAtLeastStrategy
import org.zalando.logbook.json.JsonHttpLogFormatter
import tools.jackson.databind.json.JsonMapper

private val BRUKER_ID_REGEX = Regex("""(?<!\d)\d{11}(?!\d)""")

@Configuration
@ConditionalOnNotProd
@NoCoverageAnalysis
class LogbookBeanConfiguration {

    @Bean
    fun logbook(formatter: PrettyPrintingLogbookFormatter, jwtClaimsExtractor: AttributeExtractor) =
        Logbook.builder()
            .strategy(StatusAtLeastStrategy(BAD_REQUEST.value()))
            .bodyFilter { _, body ->
                BRUKER_ID_REGEX.replace(body, "<brukerId>")
            }
            .condition(
                exclude(
                    requestTo("**/internal/**"),
                    requestTo("**/monitoring/**"),
                    requestTo("**/actuator/**"),
                    requestTo("https://graph.microsoft.com/v1.0/organization"),
                ),
            )
            .attributeExtractor(jwtClaimsExtractor)
            .sink(DefaultSink(formatter, DefaultHttpLogWriter()))
            .build()

    @Component
    class PrettyPrintingLogbookFormatter(private val mapper: JsonMapper) : HttpLogFormatter {
        private val delegate = JsonHttpLogFormatter(mapper, true)

        override fun format(precorrelation: Precorrelation, request: HttpRequest) =
            prettyPrint(delegate.format(precorrelation, request))

        override fun format(correlation: Correlation, response: HttpResponse) =
            prettyPrint(delegate.format(correlation, response))

        private fun prettyPrint(raw: String) =
            runCatching {
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(raw))
            }.getOrDefault(raw)
    }

    @Component
    class NimbusJwtClaimsExtractor : AttributeExtractor {

        override fun extract(request: HttpRequest): HttpAttributes {
            val auth = request.headers.getFirst(AUTHORIZATION) ?: return EMPTY
            return HttpAttributes(parse(auth.removePrefix("Bearer ")).jwtClaimsSet.claims)
        }
    }

}

