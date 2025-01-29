package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

interface Pingable {

    fun ping() : Map<String, String>
    fun pingEndpoint() : String
    fun name() : String = javaClass.simpleName
    fun isEnabled() : Boolean = true
}