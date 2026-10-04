package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.sikkerhetstjenesten.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.sikkerhetstjenesten.felles.utils.extensions.TimeExtensions.månederSidenIdag
import no.nav.tilgangsmaskin.ansatt.AnsattId
import java.time.Clock
import java.time.LocalDate


object DomainExtensions {

    fun <T> withAnsattContext(ansattId: AnsattId, block: () -> T): T =
        withMDC(USER_ID to ansattId.verdi, block = block)

    enum class Dødsperiode {
        MND_0_6,
        MND_7_12,
        MND_13_24,
        MND_OVER_24
    }
    fun LocalDate.intervallSiden(clock: Clock) =
        when (månederSidenIdag(clock)) {
            in 0..6 -> Dødsperiode.MND_0_6
            in 7..12 -> Dødsperiode.MND_7_12
            in 13..24 -> Dødsperiode.MND_13_24
            else -> Dødsperiode.MND_OVER_24
        }
}