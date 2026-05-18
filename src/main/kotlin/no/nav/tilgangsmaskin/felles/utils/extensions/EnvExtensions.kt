package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springframework.core.env.Environment
import org.springframework.core.env.getRequiredProperty

@NoCoverageAnalysis
object EnvExtensions {
     fun Environment.schemaRegistryUrl() =
        getRequiredProperty<String>("kafka.schema.registry")

    @NoCoverageAnalysis
     fun Environment.userInfo() =
        "${getRequiredProperty<String>("kafka.schema.registry.user")}:${getRequiredProperty<String>("kafka.schema.registry.password")}"

}