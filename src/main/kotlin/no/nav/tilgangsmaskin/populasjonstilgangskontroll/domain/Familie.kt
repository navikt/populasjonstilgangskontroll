package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

data class Familie(val mor: BrukerId? = null, val far: BrukerId? = null, val barn: List<BrukerId> = emptyList()) {
    companion object {
        val INGEN = Familie()
    }
}