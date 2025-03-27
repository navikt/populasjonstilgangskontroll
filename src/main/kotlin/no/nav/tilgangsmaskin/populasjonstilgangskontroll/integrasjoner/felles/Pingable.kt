package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

interface Pingable {

    fun ping() : Any
    val pingEndpoint : String
    val name : String
    val isEnabled : Boolean
}