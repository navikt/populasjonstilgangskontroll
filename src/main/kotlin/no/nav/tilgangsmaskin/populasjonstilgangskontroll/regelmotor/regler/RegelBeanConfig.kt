package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE

@Configuration
class RegelBeanConfig {

    @Bean
    @Qualifier(KJERNE)
    fun kjerneregelsett(regler: List<KjerneRegel>) =
        RegelSett(KJERNE_REGELTYPE to regler.sortedWith(INSTANCE))

    @Bean
    @Qualifier(OVERSTYRBAR)
    fun overstyrbartRegelsett(regler: List<OverstyrbarRegel>) =
        RegelSett(OVERSTYRBAR_REGELTYPE to regler.sortedWith(INSTANCE))

    @Bean
    @Qualifier(KOMPLETT)
    fun komplettRegelsett(@Qualifier(KJERNE) kjerne : RegelSett, @Qualifier(OVERSTYRBAR) overstyrbart: RegelSett) =
        RegelSett(KOMPLETT_REGELTYPE to kjerne.regler + overstyrbart.regler)
}