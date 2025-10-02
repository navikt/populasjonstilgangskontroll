package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.actuate.health.Status.*
import org.springframework.boot.actuate.health.Status
import org.springframework.boot.actuate.health.StatusAggregator
import org.springframework.stereotype.Component

@Component
class OutOfServiceIgnoringStatusAggregator : StatusAggregator {
    override fun getAggregateStatus(statuses: Set<Status>): Status {
        val filtered = statuses.filter { it != OUT_OF_SERVICE }
        return when {
            filtered.contains(DOWN) -> DOWN
            filtered.contains(UP) -> UP
            else -> UNKNOWN
        }
    }
}