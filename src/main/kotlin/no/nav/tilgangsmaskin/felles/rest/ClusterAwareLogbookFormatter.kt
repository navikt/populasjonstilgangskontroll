package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.ConditionalOnDev
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
class ClusterAwareLogbookFormatter(private val jsonMapper: JsonMapper) : HttpLogFormatter {
    private val delegate  = JsonHttpLogFormatter(jsonMapper, true)

    override fun format(precorrelation: Precorrelation, request: HttpRequest): String =
        format(delegate.format(precorrelation, request))

    override fun format(correlation: Correlation, response: HttpResponse): String =
        format(delegate.format(correlation, response))

    private fun format(logLine: String) =
            prettyPrintJson(logLine)

    private fun prettyPrintJson(raw: String) =
        runCatching {
            jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonMapper.readTree(raw))
        }.getOrDefault(raw)
}