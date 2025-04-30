package no.nav.tilgangsmaskin.regler.motor

import jakarta.annotation.PostConstruct
import java.util.*
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
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

@ConfigurationProperties("gruppe")
data class GlobaleGrupper(val strengt: UUID, val nasjonal: UUID, val utland: UUID,
                          val udefinert: UUID, var fortrolig: UUID, val egenansatt: UUID) {

    @PostConstruct
    fun setIDs() {
        GlobalGruppe.setIDs(
                mapOf(
                        "gruppe.strengt" to strengt,
                        "gruppe.nasjonal" to nasjonal,
                        "gruppe.utland" to utland,
                        "gruppe.udefinert" to udefinert,
                        "gruppe.fortrolig" to fortrolig,
                        "gruppe.egenansatt" to egenansatt))
    }
}