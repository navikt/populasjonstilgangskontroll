package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat



class TilgangskontrollTest() {
    val tilgangsService= TilgangsService()

    @Test
    fun `Tilgang til applikasjonPolulasjonen er ok`() {
        val response =  tilgangsService.validerTilgang("12345678911")

        assertThat(response).isEqualTo(true)

    }
    @Test
    fun `Tilgang til populasjonen er ikke ok`(){
        val response = tilgangsService.validerTilgang("23456789111")
        assertThat(response).isEqualTo(false)
    }
}

