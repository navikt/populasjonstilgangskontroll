package no.nav.tilgangsmaskin

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ProjectConfig : AbstractProjectConfig() {
    override val extensions: List<Extension> = listOf(GruppeInitializer)
}

private object GruppeInitializer : BeforeSpecListener {
    private val initialized = AtomicBoolean(false)

    override suspend fun beforeSpec(spec: Spec) {
        if (initialized.compareAndSet(false, true)) {
            val props = Properties().apply {
                GruppeInitializer::class.java.classLoader
                    .getResourceAsStream("test.properties")
                    ?.use { load(it) }
            }
            EntraGlobalGruppe.setIDs(
                EntraGlobalGruppe.entries.associate { it.property to UUID.fromString(props.getProperty(it.property)) }
            )
        }
    }
}

