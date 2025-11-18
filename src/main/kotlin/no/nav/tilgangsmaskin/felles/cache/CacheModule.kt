package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS
import com.fasterxml.jackson.annotation.JsonTypeInfo.Value.construct
import no.nav.boot.conditionals.ConditionalOnNotProd
import tools.jackson.core.Version.unknownVersion
import tools.jackson.databind.AnnotationIntrospector
import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import tools.jackson.databind.module.SimpleModule

@ConditionalOnNotProd
/**
Dette er en modul for Jackson-serialisering som legger til en egendefinert AnnotationIntrospector. Denne introspektoren styrer hvordan typeinformasjon håndteres ved serialisering og deserialisering, slik at objekter får med seg typeinformasjon i JSON-feltet @class. Modulen aktiveres freløpigkun utenfor produksjonsmiljø.
 */
class CacheModule : SimpleModule() {
    override fun setupModule(ctx: SetupContext) {
        ctx.insertAnnotationIntrospector(object : AnnotationIntrospector() {
            override fun findTypeResolverBuilder(config: MapperConfig<*>, ann: Annotated) =
                StdTypeResolverBuilder().init(
                    construct(CLASS, PROPERTY, "@class", null, true, true), null)
            override fun version() = unknownVersion()
        })
    }
}