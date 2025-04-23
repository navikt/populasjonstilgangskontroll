package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIdentifikatorer
import no.nav.tilgangsmaskin.ansatt.entra.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomOperasjoner
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class AnsattTjeneste(
        private val entra: EntraTjeneste,
        private val ansatte: NomOperasjoner,
        private val brukere: BrukerTjeneste,
        private val accessor: TokenClaimsAccessor) {
    private val log = getLogger(javaClass)
    fun ansatt(ansattId: AnsattId) =
        entra.ansatt(ansattId).let { ansatt ->
            val grupperFraToken = accessor.globaleGrupper()
            // log.info("Ansatt {} er medlem av følgende globale grupper {}", ansattId, grupperFraToken)
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let {
                runCatching {
                    brukere.utvidetFamilie(it.verdi)
                }.getOrNull()
            }
            Ansatt(AnsattIdentifikatorer(ansattId, ansatt.oid), ansattBruker, ansatt.grupper, grupperFraToken).also {
                log.info("Ansatt $ansattId er medlem av følgende sammenslåtte grupper ${it.grupper}", ansattId)
            }
        }
}



