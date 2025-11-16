package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS
import com.fasterxml.jackson.annotation.JsonTypeInfo.Value
import tools.jackson.core.Version.unknownVersion
import tools.jackson.databind.AnnotationIntrospector
import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import tools.jackson.databind.module.SimpleModule

/**
    Denne modulen konfigurerer Jackson til å serialisere classes slik at de inkluderer typeinformasjon i JSON-representasjonen.
    Tas inn i produksjon når Jackson 3.0 er i bruke, antagelig høsten 2025.
 */
class CacheModule : SimpleModule() {
    override fun setupModule(ctx: SetupContext) {
        val custom = object : AnnotationIntrospector() {
            override  fun findTypeResolverBuilder(config: MapperConfig<*>, ann: Annotated): StdTypeResolverBuilder {
                    val value = Value.construct(
                        CLASS,
                        PROPERTY,
                        "@class",
                        null,
                        true,
                        true
                    )
                    return StdTypeResolverBuilder().init(value, null)
            }
            override fun version() = unknownVersion()
        }
        ctx.insertAnnotationIntrospector(custom)
    }
}