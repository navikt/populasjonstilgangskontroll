package no.nav.tilgangsmaskin.ansatt

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.Ansatt.AnsattIds
import no.nav.tilgangsmaskin.ansatt.entra.Entra
import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class AnsattTjeneste(private val entra: Entra, private val ansatte: Nom,
                     private val brukere: BrukerTjeneste,
                     private val accessor: TokenClaimsAccessor) {
    private val log = getLogger(javaClass)

    fun ansatt(ansattId: AnsattId) =
        entra.ansatt(ansattId).let { ansatt ->
            val ansattFnr = ansatte.fnrForAnsatt(ansattId)
            val ansattBruker = ansattFnr?.let {
                runCatching {
                    brukere.utvidetFamilie(it.verdi)
                }.getOrNull()
            }
            Ansatt(AnsattIds(ansattId, ansatt.oid), ansattBruker, ansatt.grupper + accessor.globaleGrupper())
        }
}



