package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat



class TilgangskontrollTest() {
    val tilgangsService= TilgangsService(mocckPdlClient())

    private fun mocckPdlClient(): PdlGraphClient {
        TODO("Not yet implemented")
    }

    @Test
    fun `Tilgang til applikasjonPolulasjonen er ok`() {
        val response =  tilgangsService.validerTilgang("12345678911", "n999999")

        assertThat(response).isEqualTo(true)

    }
    @Test
    fun `Tilgang til populasjonen er ikke ok`(){
        val response = tilgangsService.validerTilgang("23456789111", "n999999")
        assertThat(response).isEqualTo(false)
    }
    @Test
    fun `Tilgang til applikasjonPolulasjonen er ok for flere identer`(){
        val listeFnr = listOf("234567890112", "12345678911")
        val response = tilgangsService.validerTilgangBulk(listeFnr, "n999999")
        assertThat(response[0].ansatt_har_tilgang).isEqualTo(false)
        assertThat(response[1].ansatt_har_tilgang).isEqualTo(true)
    }
}

