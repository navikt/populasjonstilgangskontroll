package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.felles.Generated
import org.springframework.core.env.Environment
import org.springframework.core.env.getRequiredProperty

@Generated
object EnvExtensions {
     fun Environment.schemaRegistryUrl() =
        getRequiredProperty<String>("kafka.schema.registry")

    @Generated
     fun Environment.userInfo() =
        "${getRequiredProperty<String>("kafka.schema.registry.user")}:${getRequiredProperty<String>("kafka.schema.registry.password")}"

}