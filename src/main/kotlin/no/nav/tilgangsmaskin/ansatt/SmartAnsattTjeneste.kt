package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.ConditionalOnDev
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.entra.harNasjonalTilgang

import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
@ConditionalOnDev
class SmartAnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                          private val brukere: BrukerTjeneste,
                          private val token: Token) : AnsattOperations {
    private val log = getLogger(javaClass)

    override fun ansatt(ansattId: AnsattId) =
        with(token.globaleGrupper) {
            if (harNasjonalTilgang()) {
                log.info("$ansattId har tilgang til nasjonal gruppe, slår ikke opp i Entra")
                ansattMedGrupperFra(ansattId, this)
            } else {
                ansattMedGrupperFra(ansattId, this + entra.grupper(ansattId))
            }
        }

    private fun ansattMedGrupperFra(ansattId: AnsattId, grupper: Set<EntraGruppe>): Ansatt {
        val ansattBruker = ansatte.fnrForAnsatt(ansattId)?.let {
            runCatching {
                brukere.utvidetFamilie(it.verdi)
            }.getOrNull()
        }
        return Ansatt(ansattId, ansattBruker, grupper)
    }
}

@Service
@Timed
@ConditionalOnProd
class NaivAnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                         private val brukere: BrukerTjeneste,
                         private val token: Token) : AnsattOperations {

    override fun ansatt(ansattId: AnsattId): Ansatt {
        val grupper = entra.grupper(ansattId)
        val ansattFnr = ansatte.fnrForAnsatt(ansattId)
        val ansattBruker = ansattFnr?.let {
            runCatching {
                brukere.utvidetFamilie(it.verdi)
            }.getOrNull()
        }
        return Ansatt(ansattId, ansattBruker, grupper + token.globaleGrupper)
    }
}

@FunctionalInterface
interface AnsattOperations {
    fun ansatt(ansattId: AnsattId): Ansatt
}




