package no.nav.tilgangsmaskin.felles.utils

interface MessagePublisher {

    fun publish(header: String, msg: String)
}