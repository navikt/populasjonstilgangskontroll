package no.nav.tilgangsmaskin.regler.motor

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import java.net.URI

data class RegelMetadata(val gruppeMetadata: GruppeMetadata) {

    val kode = gruppeMetadata.meta.name
    val begrunnelse get() = resolve("begrunnelse")
    val kortNavn get() = resolve("kortnavn")
    val navn = gruppeMetadata.name

    private fun resolve(suffix: String) =
        with("${gruppeMetadata.meldingsnøkkel}.$suffix") {
            messageSource.getMessage(this, null, this, getLocale())!!
        }

    companion object {
        lateinit var messageSource: MessageSource

        val TYPE_URI = URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")
        const val DETAIL_MESSAGE_CODE: String =
            "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.detail"
    }
}