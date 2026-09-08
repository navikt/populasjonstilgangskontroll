package no.nav.tilgangsmaskin.felles.rest.notifikasjon

@FunctionalInterface
interface Auditor {
    fun info(message: String, t: Throwable? = null)
}
