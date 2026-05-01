package no.nav.tilgangsmaskin

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode.Concurrent

class GlobalKotestConfig : AbstractProjectConfig() {
    override val testExecutionMode = Concurrent
}