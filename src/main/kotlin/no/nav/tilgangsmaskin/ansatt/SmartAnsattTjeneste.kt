package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.ConditionalOnDev
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIds
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
@ConditionalOnDev
class SmartAnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                          private val brukere: BrukerTjeneste,
                          private val accessor: TokenClaimsAccessor) : AnsattOperations {
    private val log = getLogger(javaClass)

    override fun ansatt(ansattId: AnsattId): Ansatt {
        val grupperFraToken = accessor.globaleGrupper()
        if (grupperFraToken.any { it.id == NASJONAL.id }) {
            log.info("Ansatt $ansattId har tilgang til nasjonal gruppe,slår ikke opp i Entra")
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let {
                runCatching {
                    brukere.utvidetFamilie(it.verdi)
                }.getOrNull()
            }
            return Ansatt(AnsattIds(ansattId), ansattBruker, grupperFraToken)
        } else {
            entra.ansatt(ansattId).let { ansatt ->
                val ansattFnr = ansatte.fnrForAnsatt(ansattId)
                val ansattBruker = ansattFnr?.let {
                    runCatching {
                        brukere.utvidetFamilie(it.verdi)
                    }.getOrNull()
                }
                return Ansatt(AnsattIds(ansattId), ansattBruker, ansatt.grupper + grupperFraToken)
            }
        }
    }
}

@Service
@Timed
@ConditionalOnProd
class NaivAnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                         private val brukere: BrukerTjeneste,
                         private val accessor: TokenClaimsAccessor) : AnsattOperations {
    private val log = getLogger(javaClass)

    override fun ansatt(ansattId: AnsattId): Ansatt {
        entra.ansatt(ansattId).let { ansatt ->
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let {
                runCatching {
                    brukere.utvidetFamilie(it.verdi)
                }.getOrNull()
            }
            return Ansatt(AnsattIds(ansattId), ansattBruker, ansatt.grupper + accessor.globaleGrupper())
        }
    }
}

interface AnsattOperations {
    fun ansatt(ansattId: AnsattId): Ansatt
}




