package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.zalando.logbook.Correlation
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.HttpResponse
import org.zalando.logbook.Precorrelation
import org.zalando.logbook.core.DefaultHttpLogFormatter

@Primary
@Component
@ConditionalOnLocalOrTest
class LocalLogbookFormatter : HttpLogFormatter {
    private val delegate  = DefaultHttpLogFormatter()

    override fun format(precorrelation: Precorrelation, request: HttpRequest): String =
        delegate.format(precorrelation, request)

    override fun format(correlation: Correlation, response: HttpResponse): String =
        delegate.format(correlation, response)

}
