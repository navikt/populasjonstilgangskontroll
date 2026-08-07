package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.ConditionalOnDev
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.zalando.logbook.Correlation
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.HttpResponse
import org.zalando.logbook.Precorrelation
import org.zalando.logbook.json.JsonHttpLogFormatter
import tools.jackson.databind.json.JsonMapper

@Component
@ConditionalOnDev
@Primary
class ClusterAwareJsonLogbookFormatter(private val jsonMapper: JsonMapper) : HttpLogFormatter {
    private val delegate = JsonHttpLogFormatter(jsonMapper, true)

    override fun format(precorrelation: Precorrelation, request: HttpRequest) =
        prettyPrint(delegate.format(precorrelation, request))

    override fun format(correlation: Correlation, response: HttpResponse) =
        prettyPrint(delegate.format(correlation, response))

    private fun prettyPrint(raw: String) =
        runCatching { jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMapper.readTree(raw)) }
            .getOrDefault(raw)
}