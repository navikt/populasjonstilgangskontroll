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
    fun tilBruker(person: Map<String,PdlPipRespons>, erSkjermet: Boolean): Bruker {
        person.entries.forEach { (brukerId, respons) ->
            log.info("Mapper respons {} for Bruker {}", respons,brukerId)
        }
        log.info("Mapper person {} to Bruker {}", person,person.entries.size)
        return person.entries.first().let { (brukerId, metdata) ->
             tilBruker(BrukerId(brukerId),metdata,erSkjermet)
        }
    }

    private fun tilBruker(brukerId: BrukerId, respons: PdlPipRespons, erSkjermet: Boolean): Bruker {
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
                log.trace(CONFIDENTIAL, "Mappet person {} til kandidat {}", respons, it)
            }
        }
    }

}