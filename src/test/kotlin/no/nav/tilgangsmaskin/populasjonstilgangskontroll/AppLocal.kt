package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.springframework.boot.builder.SpringApplicationBuilder

/**
 * Starter en lokal instans mot en in-memory database
 */
@EnableMockOAuth2Server
class AppLocal : App()

fun main(args: Array<String>) {
    appLocal()
        .run(*args)
    withMockOAuth2Server {   }
}

fun appLocal(): SpringApplicationBuilder =
    SpringApplicationBuilder(AppLocal::class.java)
        .profiles("local")