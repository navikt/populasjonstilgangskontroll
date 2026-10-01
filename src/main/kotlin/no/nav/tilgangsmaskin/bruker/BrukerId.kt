package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.sikkerhetstjenesten.felles.domain.BrukerId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits

data class Identifikator(@JsonValue val verdi: String) {
    init {
        require(runCatching {
            AktørId(verdi)
        }.isSuccess || runCatching {
            BrukerId(verdi)
        }.isSuccess)
    }

    @NoCoverageAnalysis
    override fun toString() = verdi.maskFnr()
}



data class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 4)
    }
}

data class Identer(val brukerId: BrukerId, val aktorId: AktørId)
