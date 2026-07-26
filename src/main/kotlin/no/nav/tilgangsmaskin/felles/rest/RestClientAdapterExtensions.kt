package no.nav.tilgangsmaskin.felles.rest

import org.springframework.security.oauth2.client.web.client.ClientRegistrationIdProcessor.DEFAULT_INSTANCE
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

inline fun <reified T : Any> RestClientAdapter.createOAuth2Client(): T =
    HttpServiceProxyFactory.builderFor(this)
        .httpRequestValuesProcessor(DEFAULT_INSTANCE)
        .build()
        .createClient(T::class.java)
