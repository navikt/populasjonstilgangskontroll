package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component

@Component
class AnsattJPAAdapter(private val repository: AnsattRepository) {

    fun fnrForAnsatt(navId: String) = repository.findByNavid(navId)?.fnr
}