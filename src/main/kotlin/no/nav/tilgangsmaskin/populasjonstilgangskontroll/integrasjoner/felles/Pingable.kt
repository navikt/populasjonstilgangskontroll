package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

interface Pingable {

    fun ping() : Any
    fun pingEndpoint() : String
    fun name() : String = javaClass.simpleName
    fun isEnabled() : Boolean = true
}