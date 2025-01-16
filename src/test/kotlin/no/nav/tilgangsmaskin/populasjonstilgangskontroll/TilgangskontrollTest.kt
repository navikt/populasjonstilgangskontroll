package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphResponse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
//import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenUtil
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach


internal class TilgangskontrollTest() {
    val pdlGraphClient = mockk<PdlGraphClient>()
   // private val tokenUtil= mockk<TokenUtil>()
    val tilgangsService= TilgangsService(pdlGraphClient)



    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        every { pdlGraphClient.hentPerson(any()) } returns createFullPdlPersonResponse()
      //  every { tokenUtil.getAppAccessTokenWithPdlScope() } returns createtokenUtilsMock()
    }

        @Test
        fun `Tilgang til applikasjonPolulasjonen er ok`() {
            val response = tilgangsService.validerTilgang("12345678911", "n999999")

            assertThat(response).isEqualTo(true)

        }

        @Test
        fun `Tilgang til populasjonen er ikke ok`() {
            val response = tilgangsService.validerTilgang("23456789111", "n999999")
            assertThat(response).isEqualTo(false)
        }

        @Test
        fun `Tilgang til applikasjonPolulasjonen er ok for flere identer`() {
            val listeFnr = listOf("23456789012", "12345678911")
            val response = tilgangsService.validerTilgangBulk(listeFnr, "n999999")
            assertThat(response[0].ansatt_har_tilgang).isEqualTo(false)
            assertThat(response[1].ansatt_har_tilgang).isEqualTo(true)
        }


    }

    private fun createtokenUtilsMock(): String {
        return ""
    }

    fun createFullPdlPersonResponse(): PdlGraphResponse {
        return PdlGraphResponse(
            errors = emptyList(),
            data = null
        )

}

