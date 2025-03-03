package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper.tilGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.slf4j.LoggerFactory

object PdlPipTilBrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
     fun tilBruker(brukerId: BrukerId, respons: PdlPipRespons, erSkjermet: Boolean): Bruker {
        return mutableListOf<GlobalGruppe>().apply {
            if  (respons.person.adressebeskyttelse.any { it in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            else if (respons.person.adressebeskyttelse.any { it == FORTROLIG })   {
                add(FORTROLIG_GRUPPE)
            }
            if ( respons.geografiskTilknytning.gtType == UDEFINERT) {
                add(UDEFINERT_GEO_GRUPPE)
            }

            if (erSkjermet)  {
                add(EGEN_ANSATT_GRUPPE)
            }
        }.toTypedArray().let {

            Bruker(brukerId,tilGeoTilknytning(respons.geografiskTilknytning), *it).also {
                log.info(CONFIDENTIAL, "Mappet person {} til bruker {}", respons, it)
            }
        }
    }

}