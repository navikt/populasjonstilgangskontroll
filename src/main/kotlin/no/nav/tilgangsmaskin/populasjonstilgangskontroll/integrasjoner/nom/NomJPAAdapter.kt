package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component

@Component
class NomJPAAdapter(private val repository: NomRepository) {

    fun fnrForAnsatt(navId: String) = repository.findByNavid(navId).fnr
}