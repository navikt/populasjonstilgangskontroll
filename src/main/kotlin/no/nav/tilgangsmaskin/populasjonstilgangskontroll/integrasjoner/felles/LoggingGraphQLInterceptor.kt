package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.SyncGraphQlClientInterceptor

class LoggingGraphQLInterceptor : SyncGraphQlClientInterceptor {

    private val log = LoggerFactory.getLogger(LoggingGraphQLInterceptor::class.java)

    override fun intercept(req: ClientGraphQlRequest, chain: SyncGraphQlClientInterceptor.Chain) =
        chain.next(req).also {
            log.trace(CONFIDENTIAL,"Eksekverer {} med variabler {}", req.document, req.variables)
        }
}