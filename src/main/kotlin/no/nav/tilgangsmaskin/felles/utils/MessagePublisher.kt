package no.nav.tilgangsmaskin.felles.utils

@FunctionalInterface
interface MessagePublisher {

    fun publish(header: String, msg: String)
}