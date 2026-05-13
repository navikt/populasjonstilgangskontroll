package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.graphql.client.SyncGraphQlClientInterceptor.Chain

class PdlGraphQLLoggingInterceptor : SyncGraphQlClientInterceptor {
    private val log = getLogger(javaClass)

    override fun intercept(req: ClientGraphQlRequest, chain: Chain) =
        chain.next(req).also {
            log.trace(CONFIDENTIAL, "Eksekverte {} med variabler {}", req.document, req.variables)
        }
}

