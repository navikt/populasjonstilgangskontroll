package no.nav.tilgangsmaskin.felles.rest.health

import java.net.URI

interface Pingable {

    fun ping(): Any?
    val pingEndpoint: URI
    val name: String
}