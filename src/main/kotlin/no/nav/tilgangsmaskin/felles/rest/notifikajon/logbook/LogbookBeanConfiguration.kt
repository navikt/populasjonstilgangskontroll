package no.nav.tilgangsmaskin.felles.rest.notifikajon.logbook

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.zalando.logbook.Logbook
import org.zalando.logbook.attributes.AttributeExtractor
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink

private val BRUKER_ID_REGEX = Regex("""(?<!\d)\d{11}(?!\d)""")

@Configuration
@ConditionalOnNotProd
@NoCoverageAnalysis
class LogbookBeanConfiguration {

    @Bean
    fun logbook(formatter: LogbookPrettyPrintingFormatter, jwtClaimsExtractor: AttributeExtractor) =
        Logbook.builder()
            .strategy(LogbookStatusAtLeastExcluding(BAD_REQUEST,NOT_FOUND))
            .bodyFilter { _, body ->
                BRUKER_ID_REGEX.replace(body) { m -> m.value.maskFnr()
                }
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

}
