package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS
import com.fasterxml.jackson.annotation.JsonTypeInfo.Value.construct
import com.fasterxml.jackson.annotation.JsonValue
import tools.jackson.core.Version.unknownVersion
import tools.jackson.databind.AnnotationIntrospector
import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.introspect.AnnotatedClass
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import tools.jackson.databind.module.SimpleModule

/**
 * Modul for Jackson 3 som legger til typeinformasjon (@class) ved serialisering,
 * men hopper over typer som serialiseres som skalarer (har @JsonValue).
 */
class JacksonTypeInfoAddingValkeyModule : SimpleModule() {
    override fun setupModule(ctx: SetupContext) {
        ctx.insertAnnotationIntrospector(object : AnnotationIntrospector() {
            override fun findTypeResolverBuilder(config: MapperConfig<*>, ann: Annotated): StdTypeResolverBuilder? {
                if (ann is AnnotatedClass && hasJsonValue(ann)) return null
                return StdTypeResolverBuilder().init(
                    construct(CLASS, PROPERTY, "@class", null, true, true), null)
            }

            private fun hasJsonValue(ann: AnnotatedClass): Boolean =
                ann.rawType.declaredFields.any { it.isAnnotationPresent(JsonValue::class.java) }
                    || ann.rawType.declaredMethods.any { it.isAnnotationPresent(JsonValue::class.java) }

            override fun version() = unknownVersion()
        })
    }
}