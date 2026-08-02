package no.nav.tilgangsmaskin.felles.security

internal object OAuth2DownstreamURIContext {
    private val downstreamUri = ThreadLocal<String?>()

    fun currentUri(): String? = downstreamUri.get()

    fun set(uri: String) {
        downstreamUri.set(uri)
    }

    fun clear() {
        downstreamUri.remove()
    }
}

