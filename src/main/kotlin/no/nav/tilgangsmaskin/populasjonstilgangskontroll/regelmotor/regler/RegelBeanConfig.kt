package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KOMPLETT_REGELTYPE
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE

@Configuration
class RegelBeanConfig {

    @Bean
    @Qualifier(KJERNE)
    fun kjerneregelsett(regler: List<Regel>) =
        RegelSett(KJERNE_REGELTYPE to regler.filterIsInstance<KjerneRegel>().sortedWith(INSTANCE))

    @Bean
    @Qualifier(KOMPLETT)
    fun komplettRegelsett(regler: List<Regel>) =
        RegelSett(KOMPLETT_REGELTYPE to regler.sortedWith(INSTANCE))
}