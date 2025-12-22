package no.nav.tilgangsmaskin.felles.cache

import org.springframework.context.ApplicationEvent

data class CacheInnslagFjernetHendelse(val nøkkel: String) : ApplicationEvent(nøkkel)