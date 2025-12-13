package no.nav.tilgangsmaskin.felles.utils.extensions

import org.springframework.core.env.Environment
import org.springframework.core.env.getRequiredProperty

object EnvExtensions {
     fun Environment.schemaRegistryUrl() =
        getRequiredProperty<String>("kafka.schema.registry")

     fun Environment.schemaRegistryUserInfo() =
        "${getRequiredProperty<String>("kafka.schema.registry.user")}:${getRequiredProperty<String>("kafka.schema.registry.password")}"

}