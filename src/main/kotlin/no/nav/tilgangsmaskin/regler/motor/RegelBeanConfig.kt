package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
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
    fun komplettRegelsett(@Qualifier(KJERNE) kjerne: RegelSett, @Qualifier(OVERSTYRBAR) overstyrbart: RegelSett) =
        RegelSett(KOMPLETT_REGELTYPE to kjerne + overstyrbart)
}