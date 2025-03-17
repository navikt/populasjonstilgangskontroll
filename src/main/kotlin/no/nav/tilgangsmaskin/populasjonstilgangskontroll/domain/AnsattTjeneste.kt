package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import io.micrometer.core.annotation.Timed
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.boot.conditionals.ConditionalOnDev
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.ConditionalOnNotDev
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
@ConditionalOnNotDev
class NomAwareAnsattTjeneste(private val entra: EntraTjeneste, private val nom: NomTjeneste, private val pdl: BrukerTjeneste) :  AnsattOperasjoner
{
    private val log = getLogger(NomAwareAnsattTjeneste::class.java)
    override fun ansatt(ansattId: AnsattId)  =
        runBlocking {
            val entra =  async { entra.ansatt(ansattId) }.await()
            val ansattFnr =  async { nom.fnrForAnsatt(ansattId) }.await()
            val ansattBruker = ansattFnr?.let { pdl.bruker(it) }
            Ansatt(ansattBruker,AnsattIdentifikatorer(ansattId, entra.oid, ansattFnr), entra.grupper).also {
                log.trace(CONFIDENTIAL,"Ansatt er {}", it)
            }
        }
}

@Service
@Timed
@ConditionalOnDev
class NomIgnoringAnsattTjeneste(private val entra: EntraTjeneste) :  AnsattOperasjoner {
    private val log = getLogger(NomIgnoringAnsattTjeneste::class.java)
    override fun ansatt(ansattId: AnsattId)  =
        with(entra.ansatt(ansattId)) {
            Ansatt(null, AnsattIdentifikatorer(ansattId, oid), grupper).also {
                log.trace(CONFIDENTIAL,"Ansatt er {}", it)
            }
        }
}

interface AnsattOperasjoner {
    fun ansatt(ansattId: AnsattId) : Ansatt
}