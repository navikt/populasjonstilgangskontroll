package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.IDENT
import org.junit.jupiter.api.Test

class TestStuff {

    @Test
    fun doit() {
        println(jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString( mapOf(IDENT to "111111111111") ))
    }
}