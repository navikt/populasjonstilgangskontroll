package no.nav.tilgangsmaskin.felles.rest.notifikajon

@FunctionalInterface
interface Auditor {
    fun info(message: String, t: Throwable? = null)
}
